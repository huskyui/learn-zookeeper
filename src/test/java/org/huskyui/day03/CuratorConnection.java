package org.huskyui.day03;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author huskyui
 */
public class CuratorConnection {

    public static void main(String[] args) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183")
                .sessionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .namespace("create")
                .build();
        client.start();
        System.out.println(client.getState());
        client.close();
    }
}
