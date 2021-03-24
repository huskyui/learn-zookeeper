package org.huskyui.day01;

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
public class ZKSet {

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
    public void set1() throws Exception{
        // arg1: 节点路径
        // arg2: 节点修改的数据
        // arg3: 版本号 -1代表版本号不作为修改条件
        Stat stat = zooKeeper.setData("/set/node1","node13".getBytes(),2);
        // 节点的版本号
        System.out.println(stat.getVersion());
        // 节点的创建事件
        System.out.println(stat.getCtime());
    }

    @Test
    public void set2() throws Exception{
        zooKeeper.setData("/set/node2", "node21".getBytes(), -1, new AsyncCallback.StatCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, Stat stat) {
                // o 代表处理成功
                System.out.println(rc);
                // 修改节点的路径
                System.out.println(path);
                // 上下文的参数对象
                System.out.println(ctx);
                System.out.println(stat.getVersion());

            }
        },"I am context");
        Thread.sleep(50000);
        System.out.println("结束");
    }
}
