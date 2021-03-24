package org.huskyui.day02;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.huskyui.day2.ZKConnectionWatcher;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author huskyui
 */
public class MyConfigCenter implements Watcher {
    // zk的连接串
    String IP = "127.0.0.1:2181";
    // latch
    CountDownLatch latch = new CountDownLatch(1);

    //
    static ZooKeeper zooKeeper;

    // 用于本地存储配置信息
    private String url;
    private String username;
    private String password;




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
                    zooKeeper = new ZooKeeper(IP,6000,new ZKConnectionWatcher());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }else if(event.getType() == Event.EventType.NodeDataChanged){
            initValue();
        }
    }

    public MyConfigCenter(){
        initValue();
    }

    public void initValue(){
        try{
            zooKeeper = new ZooKeeper(IP,5000,this);
            latch.await();
            this.url = new String(zooKeeper.getData("/config/url",true,null));
            this.username = new String(zooKeeper.getData("/config/username",true,null));
            this.password = new String(zooKeeper.getData("/config/password",true,null));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        MyConfigCenter myConfigCenter = new MyConfigCenter();
        for (int i = 0;i<50;i++){
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("url:"+myConfigCenter.getUrl());
            System.out.println("username:"+myConfigCenter.getUsername());
            System.out.println("password:" +myConfigCenter.getPassword());
        }


    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
