package org.huskyui.day02;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class MyLock {
    String IP = "127.0.0.1:2181";
    CountDownLatch latch = new CountDownLatch(1);
    // zookeeper 配置信息
    ZooKeeper zooKeeper;

    private final static String LOCK_ROOT_PATH = "/Locks";
    private final static String LOCK_NODE_NAME = "Lock_";
    private String lockPath;

    /**
     * 1.每个客户端往/Locks下创建临时有序节点/Locks/Lock
     * 000000001
     * 2.客户端取得/Locks下子节点，并进行排序，判断排在最前面的是否为自己，如果自己的
     * 锁节点在第一位，代表获取锁成功
     * 3.如果自己的锁节点不在第一位，则监听自己前一位的锁节点。例如，自己锁节点
     * Lock 000000001
     * 4.当前一位锁节点（Lock
     * 000000002）的逻辑
     * 5.监听客户端重新执行第2步逻辑，判断自己是否获得了锁
     */

    public MyLock() {
        try {
            zooKeeper = new ZooKeeper(IP, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.None) {
                        if (event.getState() == Event.KeeperState.SyncConnected) {
                            System.out.println("连接成功");
                            latch.countDown();
                        }
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }

    public void acquireLock() throws Exception {
        createLock();
        attemptLock();
    }

    Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            if (event.getType() == Event.EventType.NodeDeleted) {
                synchronized (this) {
                    watcher.notifyAll();
                }
            }
        }
    };

    private void attemptLock() throws KeeperException, InterruptedException {
        List<String> list = zooKeeper.getChildren(LOCK_ROOT_PATH, false);
        Collections.sort(list);
        int index = list.indexOf(lockPath.substring(LOCK_ROOT_PATH.length() + 1));
        if (index == 0) {
            System.out.println("获取锁成功！");
            return;
        } else {
            String path = list.get(index - 1);
            Stat stat = zooKeeper.exists(LOCK_ROOT_PATH + "/" + path, watcher);
            if (stat == null) {
                attemptLock();
            } else {
                synchronized (watcher) {
                    watcher.wait();
                }
                attemptLock();
            }
        }
    }

    public void releaseLock() throws Exception{
        zooKeeper.delete(this.lockPath,-1);
        zooKeeper.close();
        System.out.println("锁已经释放："+this.lockPath);
    }

    private void createLock() throws Exception {
        Stat stat = zooKeeper.exists(LOCK_ROOT_PATH, false);
        // create if not exists
        if (stat == null) {
            zooKeeper.create(LOCK_ROOT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        // create 临时有序节点
        lockPath = zooKeeper.create(LOCK_ROOT_PATH + "/" + LOCK_NODE_NAME, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("节点创建成功：" + lockPath);
    }

    public static void main(String[] args) {
        MyLock myLock = new MyLock();
        try {
            myLock.createLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
