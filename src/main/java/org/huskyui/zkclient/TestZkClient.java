package org.huskyui.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.List;

/**
 * @author huskyui
 * @date 2020/5/14 18:10
 */

public class TestZkClient {
    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("121.36.241.65:2181",5000);
        System.out.println("zk 成功连接");

        String path = "/zk_test";
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("路径" + parentPath+"下面的节点变更，子节点为："+currentChilds);
            }
        });

        zkClient.createPersistent(path+"/a1",true);
        zkClient.createPersistent(path+"/a2",true);
        Thread.sleep(5000);
        System.out.println(zkClient.getChildren(path));
    }
}
