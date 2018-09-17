package com.cheche365.cheche.microservice;

import as.leap.vertx.rpc.WireProtocol;
import as.leap.vertx.rpc.impl.RPCClientOptions;
import as.leap.vertx.rpc.impl.RPCServerOptions;
import as.leap.vertx.rpc.impl.VertxRPCClient;
import as.leap.vertx.rpc.impl.VertxRPCServer;
import com.cheche365.cheche.core.util.ProfileProperties;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by liqiang on 7/23/15.
 */

@Component
public class MicroServiceServer {

    private Vertx vertx;

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(MicroServiceServer.class);

    @PostConstruct
    private void createVertx() {
        Config hazelcastConfig = new Config();
        TopologyHelper.setTopology(hazelcastConfig);
        hazelcastConfig.setGroupConfig(new GroupConfig().setName("MICROSERVICE_GROUP"));
        ClusterManager mgr = new HazelcastClusterManager(hazelcastConfig);
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        //fix cluster communication
        options.setClusterHost(TopologyHelper.getLocalInterface());
        final AtomicBoolean initialized = new AtomicBoolean(false);
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                this.vertx = res.result();
                initialized.set(true);
            } else {
                logger.error("initialized vertx failure", res.cause());
                initialized.set(true);
            }
        });

        while (!initialized.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }

    @PreDestroy
    private void destroyVertx() {
        if (null != vertx) {
            final AtomicBoolean destroyed = new AtomicBoolean(false);
            vertx.close(res -> {
                if (res.succeeded()) {
                    destroyed.set(true);
                } else {
                    logger.error("destroy vertx failure", res.cause());
                    destroyed.set(true);
                }
                vertx = null;
            });

            while (!destroyed.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public <T> void registerService(final Class<T> serviceClass, T serviceImpl) {
        new VertxRPCServer(new RPCServerOptions(vertx).setBusAddress(getServiceName(serviceClass))
            .addService(serviceImpl).setWireProtocol(WireProtocol.PROTOBUF));
    }

    public <T> T getService(final Class<T> serviceClass) {
        return (T) serviceMap.computeIfAbsent(getServiceName(serviceClass), (key) -> {
            RPCClientOptions<T> rpcClientOptions = new RPCClientOptions<T>(vertx)
                .setBusAddress(key).setServiceClass(serviceClass).setWireProtocol(WireProtocol.PROTOBUF);
            return new VertxRPCClient<>(rpcClientOptions).bindService();
        });
    }

    private <T> String getServiceName(Class<T> serviceClass) {
        // 由于不同产品所用profile不同，现约定对于微服务所用的serverName都以第一个active profile为准，以防止微服务用itg，而web用“itg,mock”，导致web找不到微服务
        return ProfileProperties.profile.split(",")[0] + "." + serviceClass.getName();
    }

}
