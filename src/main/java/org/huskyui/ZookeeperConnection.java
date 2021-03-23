package org.huskyui;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZookeeperConnection {
    public static void main(String[] args) throws InterruptedException, IOException {
        CountDownLatch latch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181",5000,watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                System.out.println("连接zookeeper成功");
                latch.countDown();
            }
        });
        latch.await();
        // 获取非法编号
        System.out.println(zooKeeper.getSessionId());
        zooKeeper.close();

    }
}
