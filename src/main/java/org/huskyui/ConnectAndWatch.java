package org.huskyui;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

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

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, (watchedEvent) -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();

        Stat stat = zooKeeper.exists(PREFIX, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
            }
        });


        TimeUnit.SECONDS.sleep(1000);


    }
}
