package com.hazelcast.internal.tpc;

import com.hazelcast.internal.tpc.iobuffer.IOBuffer;
import com.hazelcast.internal.tpc.nio.NioReactorBuilder;
import org.junit.After;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.hazelcast.internal.tpc.TpcTestSupport.assertCompletesEventually;
import static com.hazelcast.internal.tpc.TpcTestSupport.terminateAll;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TLSTest {

    private final List<Reactor> reactors = new ArrayList<>();

    public  ReactorBuilder newReactorBuilder(){
        return new NioReactorBuilder();
    }

    public Reactor newReactor() {
        ReactorBuilder builder = newReactorBuilder();
        Reactor reactor = builder.build();
        reactors.add(reactor);
        return reactor.start();
    }

    @After
    public void after() throws InterruptedException {
        terminateAll(reactors);
    }

    @Test
    public void test() throws InterruptedException {
        DefaultSSLEngineFactory sslEngineFactory = new DefaultSSLEngineFactory();
        Reactor reactor = newReactor();
        AsyncServerSocket serverSocket = reactor.newAsyncServerSocketBuilder()
                .setAcceptConsumer(acceptRequest -> {
                    AsyncSocket socket = reactor.newAsyncSocketBuilder(acceptRequest)
                            .setReadHandler(new DevNullReadHandler())
                            .setSSLEngineFactory(sslEngineFactory)
                            .build();
                    socket.start();
                })
                .build();

        SocketAddress serverAddress = new InetSocketAddress("127.0.0.1", 5000);
        serverSocket.bind(serverAddress);
        serverSocket.start();

        AsyncSocket clientSocket = reactor.newAsyncSocketBuilder()
                .setReadHandler(new DevNullReadHandler())
                .setSSLEngineFactory(sslEngineFactory)
                .build();
        clientSocket.start();

        CompletableFuture<Void> connect = clientSocket.connect(serverAddress);

        assertCompletesEventually(connect);
        assertNull(connect.join());
        assertEquals(serverAddress, clientSocket.getRemoteAddress());

        Thread.sleep(2000);

        for(int k=0;k<100;k++) {
            IOBuffer ioBuffer = new IOBuffer(8);
            ioBuffer.writeLong(1);
            ioBuffer.flip();
            clientSocket.writeAndFlush(ioBuffer);
        }
        Thread.sleep(15000);
    }
}
