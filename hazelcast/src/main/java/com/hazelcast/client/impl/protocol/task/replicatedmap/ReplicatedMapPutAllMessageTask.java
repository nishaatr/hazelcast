/*
 * Copyright (c) 2008-2015, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.client.impl.protocol.task.replicatedmap;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.parameters.ReplicatedMapPutAllParameters;
import com.hazelcast.client.impl.protocol.parameters.VoidResultParameters;
import com.hazelcast.client.impl.protocol.task.AbstractCallableMessageTask;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Connection;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.security.permission.ActionConstants;
import com.hazelcast.security.permission.ReplicatedMapPermission;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

public class ReplicatedMapPutAllMessageTask extends AbstractCallableMessageTask<ReplicatedMapPutAllParameters> {

    public ReplicatedMapPutAllMessageTask(ClientMessage clientMessage, Node node, Connection connection) {
        super(clientMessage, node, connection);
    }

    @Override
    protected ClientMessage call() throws Exception {

        ReplicatedMapService replicatedMapService = getService(ReplicatedMapService.SERVICE_NAME);
        ReplicatedRecordStore recordStore = replicatedMapService.getReplicatedRecordStore(parameters.name, true);
        final int size = parameters.key.size();
        for (int i = 0; i < size; i++) {
            recordStore.put(parameters.key.get(i), parameters.value.get(i));

        }
        return VoidResultParameters.encode();
    }

    @Override
    protected ReplicatedMapPutAllParameters decodeClientMessage(ClientMessage clientMessage) {
        return ReplicatedMapPutAllParameters.decode(clientMessage);
    }

    @Override
    public String getServiceName() {
        return ReplicatedMapService.SERVICE_NAME;
    }

    @Override
    public String getDistributedObjectName() {
        return parameters.name;
    }

    @Override
    public String getMethodName() {
        return "putAll";
    }

    @Override
    public Permission getRequiredPermission() {
        return new ReplicatedMapPermission(parameters.name, ActionConstants.ACTION_PUT);
    }

    @Override
    public Object[] getParameters() {
        final int size = parameters.key.size();
        Map map = new HashMap();
        for (int i = 0; i < size; i++) {
            map.put(parameters.key.get(i), parameters.value.get(i));
        }
        return new Object[]{parameters.key};
    }
}

