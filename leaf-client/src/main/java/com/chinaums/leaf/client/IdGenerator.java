package com.chinaums.leaf.client;

import com.sankuai.inf.leaf.IDGen;
import com.sankuai.inf.leaf.common.Result;
import com.sankuai.inf.leaf.common.Status;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MonitorConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

import java.util.concurrent.ConcurrentHashMap;

public class IdGenerator {
    private String serverName;
    private String zkAddrs;
    private IDGen instance;
    private ConcurrentHashMap<String, IDGen> stickyInstanceMap = new ConcurrentHashMap<>();
    private final boolean STICKY_DEFAULT = false;

    /**
     * @param serverName
     * @param zkAddrList
     */
    public IdGenerator(String serverName, String[] zkAddrList) {
        if (null == serverName || serverName.length() == 0) {
            throw new IllegalArgumentException("serverName is illegal");
        }
        this.serverName = serverName;
        if (null == zkAddrList || zkAddrList.length == 0) {
            throw new IllegalArgumentException("zkAddress is illegal");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("zookeeper://").append(zkAddrList[0]);
            if (zkAddrList.length > 1) {
                sb.append("?backup=");
                for (int i = 1; i < zkAddrList.length; i++) {
                    sb.append(zkAddrList[i]).append(",");
                }
                this.zkAddrs = sb.substring(0, sb.length() - 1);
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
            instance = importReference(null, STICKY_DEFAULT);
        }
        return instance;
    }

    private IDGen getStickyInstance(String key) {
        IDGen instance = stickyInstanceMap.get(key);
        if (null != instance) {
            return instance;
        }

        instance = importReference(key, true);
        IDGen prev = stickyInstanceMap.putIfAbsent(key, instance);
        if (null != prev) {
            instance = prev;
        }

        return instance;
    }

    private IDGen importReference(String key, boolean sticky) {
        final ApplicationConfig application = new ApplicationConfig();
        application.setName("leaf-client-of-" + serverName);

        RegistryConfig registry = new RegistryConfig();
        registry.setAddress(zkAddrs);
        registry.setFile("leaf/dubbo.cache." + serverName);

        MonitorConfig monitor = new MonitorConfig();
        monitor.setProtocol("registry");

        ReferenceConfig<IDGen> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface(IDGen.class);
        reference.setSticky(sticky);
        reference.setVersion("1.0.0");

        reference.setMonitor(monitor);

        return reference.get();
    }

    public long nextRandomId() {
        Result result = getInstance().get();
        if (result.getStatus() != Status.SUCCESS) {
            throw new IllegalStateException("leaf service is unavailable");
        }
        return result.getId();
    }

    public long nextSequenceId(String key) {
        if (null == key || key.length() == 0) {
            throw new IllegalArgumentException();
        }
        Result result = getStickyInstance(key).get(key);
        if (result.getStatus() != Status.SUCCESS) {
            throw new IllegalStateException("leaf service is unavailable");
        }
        return result.getId();
    }

}
