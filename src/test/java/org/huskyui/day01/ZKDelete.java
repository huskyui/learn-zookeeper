package org.huskyui.day01;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZKDelete {
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
    public void delete1() throws Exception{
        // arg1: 删除节点的路径
        // arg2: 数据版本信息 -1 代表删除节点时不考虑版本信息
        zooKeeper.delete("/delete/node1",-1);
    }

    @Test
    public void delete2() throws Exception{
        zooKeeper.delete("/delete/node2", -1, new AsyncCallback.VoidCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx) {
                // 0 represent delete success
                System.out.println(rc);
                // the path of node
                System.out.println(path);
                // 上下文参数对象
                System.out.println(ctx);
            }
        }," i am context");
        Thread.sleep(10000);
        System.out.println("结束");
    }



}
