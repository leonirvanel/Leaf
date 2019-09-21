package com.chinaums.leaf.dubbo;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.List;

public class CustomerKeyLoadBalance extends AbstractLoadBalance {

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int chosenIndex = 0;
        if (invocation.getArguments() != null && invocation.getArguments().length >= 1 && invocation.getParameterTypes()[0].equals(String.class)) {
            String customerKey = (String) invocation.getArguments()[0];
            chosenIndex = (Math.abs(customerKey.hashCode()) & 0x7fffffff) % invokers.size();
        }
        return invokers.get(chosenIndex);
    }
}
