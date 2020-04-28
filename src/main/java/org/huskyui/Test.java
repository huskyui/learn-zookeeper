package org.huskyui;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 * @date 2020/4/27 16:31
 */

public class Test {
    private static final String PREFIX = "/mytest-sync-create-";

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
        Stat stat = zooKeeper.exists("/huskyui", (watchEvent)->{
            if(watchEvent)
        });
        logger.info("stat :{}", stat);

        zooKeeper.setData("/huskyui","data of node 3".getBytes(),stat.getVersion());

    }
}
