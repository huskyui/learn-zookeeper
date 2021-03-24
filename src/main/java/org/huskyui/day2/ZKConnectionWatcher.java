package org.huskyui.day2;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class ZKConnectionWatcher implements Watcher {
    // 计时器对象
    static CountDownLatch latch = new CountDownLatch(1);
    // 连接对象
    static ZooKeeper zooKeeper;


    @Override
    public void process(WatchedEvent event) {

        try{
            // 事件类型
            if(event.getType() == Event.EventType.None){
                if(event.getState()==Event.KeeperState.SyncConnected){
                    System.out.println("连接成功");
                    latch.countDown();
                }else if(event.getState() == Event.KeeperState.Disconnected){
                    System.out.println("断开连接");
                }else if(event.getState() == Event.KeeperState.Expired){
                    System.out.println("非法超时");
                    zooKeeper = new ZooKeeper("127.0.0.1:2181",5000,new ZKConnectionWatcher());
                } else if(event.getState() == Event.KeeperState.AuthFailed){
                    System.out.println("认证失败");
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            zooKeeper = new ZooKeeper("127.0.0.1:2181", 5000, new ZKConnectionWatcher());
            // 阻塞线程，等待连接的创建
            latch.await();
            // 获取会话id
            System.out.println(zooKeeper.getSessionId());
//            zooKeeper.addAuthInfo("digest1","itcas1:123456".getBytes());
            byte []bs = zooKeeper.getData("/node1",false,null);
            System.out.println(new String(bs));
            Thread.sleep(50000);
            zooKeeper.close();
            System.out.println("结束");



        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
