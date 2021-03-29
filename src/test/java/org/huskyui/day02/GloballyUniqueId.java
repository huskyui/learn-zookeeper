package org.huskyui.day02;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class GloballyUniqueId implements Watcher {

    String IP = "127.0.0.1:2181";
    CountDownLatch latch = new CountDownLatch(1);
    String defaultPath = "/uniqueId";
    ZooKeeper zooKeeper;


    @Override
    public void process(WatchedEvent event) {
        if(event.getType() == Event.EventType.None){
            if(event.getState() == Event.KeeperState.SyncConnected){
                System.out.println("连接成功");
                latch.countDown();
            }else if(event.getState() == Event.KeeperState.Disconnected){
                System.out.println("连接断开");
            }else if(event.getState() == Event.KeeperState.Expired){
                System.out.println("连接超时");
                try {
                    zooKeeper = new ZooKeeper(IP,6000,new GloballyUniqueId());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }else if(event.getState() == Event.KeeperState.AuthFailed){
                System.out.println("验证失败");
            }
        }
    }

    public GloballyUniqueId(){
        try {
            zooKeeper = new ZooKeeper(IP,5000,this);
            latch.await();
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
    }

    public String getUniqueId(){
        String path = "";
        try {
            path = zooKeeper.create(defaultPath,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // /uniqueId000000001
        return path.substring(9);
    }

    public static void main(String[] args) {
        GloballyUniqueId globallyUniqueId = new GloballyUniqueId();

        for (int i = 0 ;i<= 5;i++){
            String id = globallyUniqueId.getUniqueId();
            System.out.println(id);
        }
    }
}
