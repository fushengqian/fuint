package com.fuint.cache.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class RedissonFactoryBean implements DisposableBean, FactoryBean<RedissonClient> {

    private List<String> nodeAddresses;

    private RedissonClient redisson;

    @Override
    public RedissonClient getObject() throws Exception {
        Config config = new Config();
//        ClusterServersConfig clusterServer = config.useClusterServers();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        if (!CollectionUtils.isEmpty(nodeAddresses)) {
            singleServerConfig.setAddress(nodeAddresses.get(0));
        }

        this.redisson = Redisson.create(config);
        return redisson;
    }

    @Override
    public Class<?> getObjectType() {
        return RedissonClient.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public List<String> getNodeAddresses() {
        return nodeAddresses;
    }

    public void setNodeAddresses(List<String> nodeAddresses) {
        this.nodeAddresses = nodeAddresses;
    }

    @Override
    public void destroy() throws Exception {
        if (redisson != null) {
            synchronized (this) {
                if (redisson != null) {
                    redisson.shutdown();
                }
            }
        }
    }
}
