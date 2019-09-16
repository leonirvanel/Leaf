package com.sankuai.inf.leaf.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertyFactory {
    private static final Logger logger = LoggerFactory.getLogger(PropertyFactory.class);
    private static Properties prop = null;

    public static Properties getProperties() {
        if(null != prop) {
            return prop;
        }

        synchronized (PropertyFactory.class) {
            if (null != prop) {
                return prop;
            }

            String runEnv = System.getProperty("run_env", "test");
            logger.info("run_env={}", runEnv);
            try {
                //prop.load(PropertyFactory.class.getClassLoader().getResourceAsStream("leaf_" + runEnv + ".properties"));
                String propertyFile = (runEnv != null ? "leaf_" + runEnv + ".property" : "leaf.properties");
                Properties temp = new Properties();
                temp.load(PropertyFactory.class.getClassLoader().getResourceAsStream("leaf.properties"));
                prop = temp;
            } catch (IOException e) {
                logger.warn("Load Properties Ex", e);
            }
            return prop;
        }
    }


}
