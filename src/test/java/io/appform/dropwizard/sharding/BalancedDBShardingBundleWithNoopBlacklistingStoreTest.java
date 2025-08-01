/*
 * Copyright 2019 Santanu Sinha <santanu.sinha@gmail.com>
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
 *
 */

package io.appform.dropwizard.sharding;


import io.appform.dropwizard.sharding.config.ShardedHibernateFactory;
import io.appform.dropwizard.sharding.dao.testdata.entities.Order;
import io.appform.dropwizard.sharding.dao.testdata.entities.OrderItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BalancedDBShardingBundleWithNoopBlacklistingStoreTest extends BundleBasedTestBase {

    @Override
    protected DBShardingBundleBase<TestConfig> getBundle() {
        return new BalancedDBShardingBundle<TestConfig>("namespace", Order.class, OrderItem.class) {
            @Override
            protected ShardedHibernateFactory getConfig(TestConfig config) {
                return testConfig.getShards();
            }
        };
    }

    @Test
    public void testBundleWithShardBlacklisted() throws Exception {
        DBShardingBundleBase<TestConfig> bundle = getBundle();
        bundle.initialize(bootstrap);
        testConfig.getShards().getShardingOptions().setSkipNativeHealthcheck(false);
        bundle.run(testConfig, environment);
        bundle.getShardManager().blacklistShard(1);
        //one for each shard
        assertEquals(2, bundle.healthStatus().size());
    }
}
