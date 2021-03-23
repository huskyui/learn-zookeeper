package org.huskyui;


import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZKGet {

    String IP = "127.0.0.1:2181";
    ZooKeeper zooKeeper;

    @Before
    public void before() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(event.getState()==Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
    }

    @After
    public void after() throws Exception{
        zooKeeper.close();
    }

    @Test
    public void get1() throws Exception{
        Stat stat = new Stat();
        byte[] bytes = zooKeeper.getData("/get/node1",false,stat);
        System.out.println(new String(bytes));
        System.out.println(stat.getVersion());
    }

    @Test
    public void get2() throws Exception{
        zooKeeper.getData("/get/node1", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println(rc);
                System.out.println(path);
                System.out.println(new String(data));
                System.out.println(stat.getVersion());
            }
        },"I am context");
        Thread.sleep(10000);
        System.out.println("结束");

    }

}
