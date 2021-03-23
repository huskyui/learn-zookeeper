package org.huskyui;


import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class ConnectAndCreate {


    private static final String PREFIX = "/mytest-sync-create-";

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper("121.36.241.65:2181", 5000, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
            }
        }
        );
        // 这里是确定ZooKeeper已经连接
        countDownLatch.await();
        // CreatedMode.EPHEMERAL the znode will be deleted upon the client's disconnect 未来被动
        // ACL ：Access Control List;  Each node has an Access Control List (ACL) that restricts who can do what.每个节点都有acl来限制谁可以做什么
        // ZooDefs.Ids.OPEN_ACL_UNSAFE: This is a completely open ACL . 这是完全开放的ACL
        String path1 = zooKeeper.create(PREFIX, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("success create node: " + path1);
        String path2 = zooKeeper.create(PREFIX, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("success create node: " + path2);
        String path3 = zooKeeper.create(PREFIX, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("success create node: " + path3);
        System.out.println(zooKeeper.getChildren("/", false));

        // 删除节点
        /**
         * Delete the node with the given path. The call will succeed if such a node
         * exists, and the given version matches the node's version (if the given
         * version is -1, it matches any node's versions).
         * */
        zooKeeper.delete(PREFIX, 0);

        TimeUnit.SECONDS.sleep(50);
        zooKeeper.close();
    }
}
