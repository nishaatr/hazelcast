/*
 * Copyright (c) 2008-2022, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.internal.tpc.nio;

import com.hazelcast.internal.tpc.AsyncServerSocket;
import com.hazelcast.internal.tpc.AsyncSocket;
import com.hazelcast.internal.tpc.Eventloop;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.hazelcast.internal.tpc.util.CloseUtil.closeQuietly;
import static com.hazelcast.internal.tpc.util.ReflectionUtil.findStaticFieldValue;
import static java.net.StandardSocketOptions.SO_RCVBUF;
import static java.net.StandardSocketOptions.SO_REUSEADDR;
import static java.nio.channels.SelectionKey.OP_ACCEPT;

/**
 * Nio implementation of the {@link AsyncServerSocket}.
 */
public final class NioAsyncServerSocket extends AsyncServerSocket {
    private static final int DEFAULT_LATCH_TIMEOUT_SECONDS = 10;
    private static final AtomicBoolean REUSE_PORT_PRINTED = new AtomicBoolean();

    // This option is available since Java 9, so we need to use reflection.
    private static final SocketOption<Boolean> SO_REUSEPORT = findStaticFieldValue(StandardSocketOptions.class, "SO_REUSEPORT");

    private final ServerSocketChannel serverSocketChannel;
    private final Selector selector;
    private final NioEventloop eventloop;
    private final Thread eventloopThread;

    private NioAsyncServerSocket(NioEventloop eventloop) {
        try {
            this.serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            this.eventloop = eventloop;
            this.eventloopThread = eventloop.eventloopThread();
            this.selector = eventloop.selector;
            if (!eventloop.registerClosable(this)) {
                close();
                throw new IllegalStateException(eventloop + " is not running");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Opens a NioAsyncServerSocket.
     * <p/>
     * To prevent coupling to Nio, it is better to use the {@link Eventloop#openAsyncServerSocket()}.
     *
     * @param eventloop the eventloop the opened socket will be processed by.
     * @return the opened NioAsyncServerSocket.
     */
    public static NioAsyncServerSocket open(NioEventloop eventloop) {
        return new NioAsyncServerSocket(eventloop);
    }

    @Override
    public NioEventloop getEventloop() {
        return eventloop;
    }

    /**
     * Returns the underlying {@link ServerSocketChannel}.
     *
     * @return the ServerSocketChannel.
     */
    public ServerSocketChannel serverSocketChannel() {
        return serverSocketChannel;
    }

    @Override
    protected SocketAddress getLocalAddress0() throws IOException {
        return serverSocketChannel.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return serverSocketChannel.socket().getLocalPort();
    }

    @Override
    public boolean isReusePort() {
        if (SO_REUSEPORT == null) {
            return false;
        }

        try {
            return serverSocketChannel.getOption(SO_REUSEPORT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setReusePort(boolean reusePort) {
        if (SO_REUSEPORT == null) {
            if (REUSE_PORT_PRINTED.compareAndSet(false, true)) {
                logger.warning("Ignoring NioAsyncServerSocket.reusePort." +
                        "Please upgrade to Java 9+ to enable the SO_REUSEPORT option.");
            }
        } else {
            try {
                serverSocketChannel.setOption(SO_REUSEPORT, reusePort);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public boolean isReuseAddress() {
        try {
            return serverSocketChannel.getOption(SO_REUSEADDR);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setReuseAddress(boolean reuseAddress) {
        try {
            serverSocketChannel.setOption(SO_REUSEADDR, reuseAddress);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setReceiveBufferSize(int size) {
        try {
            serverSocketChannel.setOption(SO_RCVBUF, size);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public int getReceiveBufferSize() {
        try {
            return serverSocketChannel.getOption(SO_RCVBUF);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void close0() {
        closeQuietly(serverSocketChannel.socket());
        eventloop.deregisterCloseable(this);
    }


    @Override
    public void listen(int backlog) {
        // ignore; not needed for serverSocketChannel
    }

    @Override
    public void bind(SocketAddress local) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info(eventloopThread.getName() + " Binding to " + local);
            }
            serverSocketChannel.bind(local);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to bind to " + local, e);
        }
    }

    public void accept(Consumer<AsyncSocket> consumer) {
        CountDownLatch latch = new CountDownLatch(1);

        Runnable acceptTask = () -> {
            try {
                serverSocketChannel.register(selector, OP_ACCEPT, new AcceptHandler(consumer));

                if (logger.isInfoEnabled()) {
                    logger.info(eventloopThread.getName() + " ServerSocket listening at "
                            + serverSocketChannel.getLocalAddress());
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to accept", e);
            } finally {
                latch.countDown();
            }
        };

        if (!eventloop.offer(acceptTask)) {
            throw new RuntimeException("Failed to offer accept task");
        }

        try {
            latch.await(DEFAULT_LATCH_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private final class AcceptHandler implements NioSelectedKeyListener {

        private final Consumer<AsyncSocket> consumer;

        private AcceptHandler(Consumer<AsyncSocket> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void handleException(Exception e) {
            logger.severe(this + " ran into a fatal exception", e);
        }

        @Override
        public void handle(SelectionKey key) throws IOException {
            SocketChannel socketChannel = serverSocketChannel.accept();
            NioAsyncSocket socket = new NioAsyncSocket(socketChannel);

            consumer.accept(socket);

            if (logger.isInfoEnabled()) {
                logger.info("Connection Accepted: " + socketChannel.getRemoteAddress()
                        + "->" + socketChannel.getLocalAddress());
            }
        }
    }
}
