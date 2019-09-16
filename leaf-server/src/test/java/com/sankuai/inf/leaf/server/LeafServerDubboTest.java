package com.sankuai.inf.leaf.server;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.PropertyFactory;
import org.apache.dubbo.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeafServerDubboTest {

    private static Logger logger = LoggerFactory.getLogger(LeafServerDubboTest.class);

    public static void main(String[] args) {

        ApplicationConfig application = new ApplicationConfig();
        application.setName("leaf-client");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(PropertyFactory.getProperties().getProperty(Constants.LEAF_DUBBO_ZK_ADDRESS));

        ReferenceConfig<IDGen> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(IDGen.class);
        reference.setVersion("1.0.0");

        IDGen idGen = reference.get();

        for(int i = 0; i < 10; i++) {
            logger.info("idGen {}", idGen.get(null));
        }

    }

}
