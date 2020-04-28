package org.huskyui;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.Watcher.Event.EventType.NodeCreated;

/**
 * @author huskyui
 * @date 2020/4/27 16:10
 */

public class ConnectAndWatch {
    private static final String PREFIX = "/mytest-sync-create-";

    public static void main(String[] args) throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, (watchedEvent) -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
            Watcher.Event.EventType eventType = watchedEvent.getType();
            switch (eventType) {
                case NodeCreated:
                    System.out.println("node created " + watchedEvent.getPath());
                    break;
                case NodeDataChanged:
                    System.out.println("node data changed" + watchedEvent.getPath());
                    break;
                case NodeDeleted:
                    System.out.println("node delete" + watchedEvent.getPath());
                    break;
                default:
                    break;
            }
        });
        countDownLatch.await();

        TimeUnit.SECONDS.sleep(1000);


    }
}
