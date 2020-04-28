package org.huskyui;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author huskyui
 * @date 2020/4/26 16:31
 */

public class ConnectAndCreateAsync {
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    private static final String PREFIX = "/mytest-sync-create-";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, watcher -> {
            if (watcher.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        }
        );
        countDownLatch.await();
        zooKeeper.create(PREFIX, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, ((rc, path, ctx, name) -> {
            System.out.println("rc: " + rc + " path: " + path + " ctx: " + ctx + " name : " + name);
        }), null);
        TimeUnit.SECONDS.sleep(50);

    }
}
