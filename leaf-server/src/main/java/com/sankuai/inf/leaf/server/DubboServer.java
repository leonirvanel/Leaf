package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DubboServer {

    static private final Logger LOGGER = LoggerFactory.getLogger(DubboServer.class);

    @Autowired
    private SnowflakeService snowflakeService;

    @Autowired
    private SegmentService segmentService;

    private IDGen createInstance() {
        return new IDGen() {
            @Override
            public Result get(String key) {
                if (null == key ||
                        !Boolean.parseBoolean(PropertyFactory.getProperties().getProperty(Constants.LEAF_SEGMENT_ENABLE, "true"))) {
                    return snowflakeService.getId(null);
                }
                Result result = segmentService.getId(key);
                if (result.getStatus() == Status.SUCCESS) {
                    return result;
                } else {
                    return snowflakeService.getId(null);
                }
            }

            @Override
            public boolean init() {
                return true;
            }
        };
    }

    @PostConstruct
    private void export() {
        String zkRegistryAddr = PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_ZK_ADDRESS);
        int port = Integer.parseInt(PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_PORT));
        int threads = Integer.parseInt(PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_THREADS));
        int timeoutMs = Integer.parseInt(PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_TIMEOUT));
        int retries = Integer.parseInt(PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_RETRIES));

        ApplicationConfig application = new ApplicationConfig();
        application.setName("leaf-server");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(zkRegistryAddr);

        ProtocolConfig protocol = new ProtocolConfig();
        protocol.setName("dubbo");
        protocol.setPort(port);
        protocol.setThreads(threads);

        ServiceConfig<IDGen> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setInterface(IDGen.class);
        service.setRef(createInstance());
        service.setCluster("failover");
        service.setTimeout(timeoutMs);
        service.setRetries(retries);
        service.setVersion("1.0.0");

        service.export();

        LOGGER.info("dubbo service exported");
    }

}
