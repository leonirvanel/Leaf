package com.chinaums.leaf.client;

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

    private IDGen stickyInstance;

    private final boolean STICKY_DEFAULT = false;

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
            instance = importReference(zkAddrs, STICKY_DEFAULT);
        }
        return instance;
    }

    private IDGen getStickyInstance() {
        if (null != stickyInstance) {
            return stickyInstance;
        }
        synchronized (IdGenerator.class) {
            if (null != stickyInstance) {
                return stickyInstance;
            }
            stickyInstance = importReference(zkAddrs, true);
        }
        return stickyInstance;
    }

    private IDGen importReference(String zkAddrs, boolean sticky) {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("leaf-client");

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(PropertyFactory.getProperties().getProperty(zkAddrs));

        ReferenceConfig<IDGen> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(IDGen.class);
        // 是否开启粘性连接。需要趋势递增ID的client，建议开启
        reference.setSticky(sticky);
        // cluster、timeout等参数，在服务端设定
        reference.setVersion("1.0.0");

        return reference.get();
    }

    public long nextRandomId(String key) {
        Result result = getInstance().get(key);
        if (result.getStatus() != Status.SUCCESS) {
            throw new IllegalStateException("leaf服务不可用");
        }
        return result.getId();
    }

    public long nextSequenceId(String key) {
        Result result = getStickyInstance().get(key);
        if (result.getStatus() != Status.SUCCESS) {
            throw new IllegalStateException("leaf服务不可用");
        }
        return result.getId();
    }

}
