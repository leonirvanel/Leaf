package com.sankuai.inf.leaf.server.dubbo;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import com.sankuai.inf.leaf.server.Constants;
import com.sankuai.inf.leaf.server.service.SegmentService;
import com.sankuai.inf.leaf.server.service.SnowflakeService;
import org.apache.dubbo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Service
public class DubboServer {

    static private final Logger LOGGER = LoggerFactory.getLogger(DubboServer.class);

    @Autowired
    private SnowflakeService snowflakeService;

    @Autowired
    private SegmentService segmentService;

    private static final int COST_ALERT_THRESHOLD = 20;

    private IDGen createInstance() {
        return new IDGen() {
            @Override
            public Result get(String key) {
                Result result;
                long start = System.currentTimeMillis();
                if (null == key || key.length() == 0) {
                    result = snowflakeService.getId(null);
                } else {
                    result = segmentService.getId(key);
                    if (result.getStatus() != Status.SUCCESS) {
                        LOGGER.warn("ID服务[key={}]从segment降级为snowflake", key);
                        result = snowflakeService.getId(null);
                    }
                }
                long cost = System.currentTimeMillis() - start;
                LOGGER.debug("process request, key={}, result={}, cost={}", key, result, cost);
                if (cost >= COST_ALERT_THRESHOLD) {
                    LOGGER.warn("ID服务太慢了吧 rt={} threshold={}", cost, COST_ALERT_THRESHOLD);
                }

                return result;
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

        MonitorConfig monitor = new MonitorConfig();
        monitor.setProtocol("registry");

        ArgumentConfig argument = new ArgumentConfig();
        argument.setIndex(0);
        argument.setType(String.class.getName());


        MethodConfig method = new MethodConfig();
        method.setName("get");
        method.setArguments(Arrays.asList(argument));
        method.setLoadbalance("customerKey");

        ServiceConfig<IDGen> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setMonitor(monitor);
        service.setMethods(Arrays.asList(method));
        service.setInterface(IDGen.class);
        service.setRef(createInstance());
        service.setCluster("failover");
        service.setTimeout(timeoutMs);
        service.setRetries(retries);
        service.setVersion("1.0.0");

        LOGGER.info("dubbo service[{}] is exporting", IDGen.class);
        service.export();
        LOGGER.info("dubbo service[{}] exported", IDGen.class);
    }

}
