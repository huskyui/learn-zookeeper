package org.huskyui.day01;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZKExists {
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
    public void exists1() throws Exception{
        Stat stat = zooKeeper.exists("/exists",false);
        System.out.println(stat.getVersion());
    }

    @Test
    public void exists2() throws Exception{
        Map<String,String> map = new HashMap<>();
        zooKeeper.exists("/exists", false, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                System.out.println(rc);
                Map context = (Map)ctx;
                map.put(path,String.valueOf(stat.getVersion()));
            }
        },map);
        Thread.sleep(5_000);
        System.out.println("结束");
        System.out.println(map);
    }
}
