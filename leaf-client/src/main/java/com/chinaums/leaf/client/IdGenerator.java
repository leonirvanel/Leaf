package com.chinaums.leaf.client;

import com.chinaums.leaf.client.util.NumberConverter;
import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.PropertyFactory;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

public class IdGenerator {

    private String zkAddrs;

    private IDGen instance;

    /**
     * 单个地址是IP:PORT格式
     *
     * @param zkAddrList
     */
    public IdGenerator(String[] zkAddrList) {
        if (null == zkAddrList || zkAddrList.length < 1) {
            throw new IllegalArgumentException("注册中心zookeeper地址非法");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("zookeeper://").append(zkAddrList[0]);
            if (zkAddrList.length > 1) {
                sb.append("?backup=");
                for (int i = 1; i < zkAddrList.length - 1; i++) {
                    sb.append(zkAddrList[i]).append(",");
                }
                this.zkAddrs = sb.substring(1, sb.length() - 1);
            } else {
                this.zkAddrs = sb.toString();
            }
        }
    }

    private IDGen getInstance() {
        if (null != instance) {
            return instance;
        }
        synchronized (IdGenerator.class) {
            if (null != instance) {
                return instance;
            }
            instance = importReference(zkAddrs);
        }
        return instance;
    }

    private IDGen importReference(String zkAddrs) {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("leaf-client");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(PropertyFactory.getProperties().getProperty(zkAddrs));

        ReferenceConfig<IDGen> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(IDGen.class);
        // cluster、timeout等参数，在服务端设定
        reference.setVersion("1.0.0");

        return reference.get();
    }

    public long nextId(String key) {
        Result result = instance.get(key);
        if (result.getStatus() != Status.SUCCESS) {
            throw new IllegalStateException("leaf服务不可用");
        }
        return result.getId();
    }

    public String nextStringId(String key) {
        long id = nextId(key);
        return NumberConverter.convert_10radix_to_62radix(id);
    }

}
