package org.huskyui;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 * @date 2020/4/26 16:13
 */

public class ConnectAndGet {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String PREFIX = "/mytest-sync-create-";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, watcher -> {
            if (watcher.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        }
        );
        countDownLatch.await();
        System.out.println(zooKeeper.getChildren("/",false));
        zooKeeper.setData(PREFIX,"246".getBytes(),0);

        System.out.println(zooKeeper.getChildren("/",false));
    }
}
