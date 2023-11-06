package com.hazelcast.internal.partition.impl;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.properties.ClusterProperty;
import com.hazelcast.test.Accessors;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BatchReplicaUpdateTest {

    TestHazelcastInstanceFactory factory = new TestHazelcastInstanceFactory();

    @Test
    public void testFirstArrangement() {
        // configure member with high partition count
        Config config = HazelcastTestSupport.smallInstanceConfig();
        config.setProperty(ClusterProperty.PARTITION_COUNT.getName(), "" + 20000);
        HazelcastInstance member = factory.newHazelcastInstance(config);
        // trigger first arrangement of partitions
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl) Accessors.getPartitionService(member);
        PartitionStateManager partitionStateManagerSpy = spy(partitionService.getPartitionStateManager());
        partitionStateManagerSpy.initializePartitionAssignments(Collections.emptySet());
        verify(partitionStateManagerSpy, times(1)).updateStamp();
    }
}
