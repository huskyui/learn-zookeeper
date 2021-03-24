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
public class ZKWatcherGetChild {

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
    public void watcherGetChild1() throws KeeperException, InterruptedException {
        zooKeeper.getChildren("/watcher3",true);
        Thread.sleep(50_000);
        System.out.println("end");
    }

    @Test
    public void watcherGetChild2() throws Exception{
        zooKeeper.getChildren("/watcher3", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("自定义watcher");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
            }
        },null);
        Thread.sleep(50_000);
        System.out.println("end");
    }

    @Test
    public void watcherGetChild3() throws Exception{
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("customize watcher");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
                if(event.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        zooKeeper.getChildren("/watcher3",watcher);
        Thread.sleep(50_000);
    }

    @Test
    public void watcherGetChild4() throws Exception{
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("1");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
                if(event.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Watcher watcher2 = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("2");
                System.out.println("path="+event.getPath());
                System.out.println("eventType="+event.getType());
                if(event.getType() == Event.EventType.NodeChildrenChanged){
                    try {
                        zooKeeper.getChildren("/watcher3",this);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        zooKeeper.getChildren("/watcher3",watcher);
        zooKeeper.getChildren("/watcher3",watcher2);

        Thread.sleep(50_000);
        System.out.println("end");

    }




}
