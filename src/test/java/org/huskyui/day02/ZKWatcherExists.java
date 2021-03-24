package org.huskyui.day02;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZKWatcherExists {
    String IP = "127.0.0.1:2181";
    ZooKeeper zooKeeper = null;

    @Before
    public void before() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(IP, 6000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接对象参数");
                if (event.getState() == Event.KeeperState.SyncConnected) {
                    latch.countDown();
                }
                System.out.println("path:" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        latch.await();
    }

    @After
    public void after() throws InterruptedException {
        zooKeeper.close();
    }

    @Test
    public void watcherExists1() throws Exception {
        zooKeeper.exists("/watcher1", true);
        Thread.sleep(50_000);
        System.out.println("结束");
    }

    @Test
    public void watcherExists2() throws Exception {
        zooKeeper.exists("/watcher1", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        });
        Thread.sleep(50_000);
        System.out.println("结束");
    }

    @Test
    public void watcherExists3() throws Exception{
        // watcher 只能使用一次
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
                try {
                    zooKeeper.exists("/watcher1",this);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        zooKeeper.exists("/watcher1",watcher);
        Thread.sleep(50_000);
        System.out.println("结束");
    }

    @Test
    public void watcherExists4() throws Exception{
        Watcher watcher1 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher1");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        };

        Watcher watcher2 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher2");
                System.out.println("path=" + event.getPath());
                System.out.println("eventType=" + event.getType());
            }
        };
        zooKeeper.exists("/watcher1",watcher1);
        zooKeeper.exists("/watcher1",watcher2);
        Thread.sleep(80_000);
        System.out.println("结束");
    }
}
