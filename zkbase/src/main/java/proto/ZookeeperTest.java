package proto;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.EventType;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

public class ZookeeperTest implements Watcher {

    private CountDownLatch ct = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;
    private final String CONNECTION = "127.0.0.1:2181";
    private final int TIME_OUT = 30;

    public static void main(String[] args) throws Exception {
        String path = "/hydata";
        ZookeeperTest zookeeperTest = new ZookeeperTest();
        zookeeperTest.init();
        zookeeperTest.createParentNode(path, "testhydata");
        zookeeperTest.getData(path);
        zookeeperTest.updateData(path, "12312312");
        zookeeperTest.deleteData(path);
    }
    public void init() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(CONNECTION, TIME_OUT, this);
        System.out.println("zookeeper服务："+ CONNECTION+",链接中。。。。");
        ct.await();
    }

    public void createParentNode(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, this);
        if(stat != null){
            System.out.println("createParentNode----"+stat.toString() );
        }else{
            zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void updateData(String path, String data) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, this);
        if(stat != null){
            zooKeeper.setData(path, data.getBytes(),-1);
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }

    public void deleteData(String path) throws KeeperException, InterruptedException {
        Stat stat = zooKeeper.exists(path, this);
        if(stat != null){
            zooKeeper.delete(path, 1);
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }
    public void getData(String path) throws KeeperException, InterruptedException {
        byte[] bytes = zooKeeper.getData(path, true, new Stat());
        String data = new String(bytes);
        System.out.println("获取节点的数据，"+path+"---data:"+data);
    }
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == EventType.None){
            if(watchedEvent.getState() == SyncConnected){
                System.out.println("zookeeper服务："+ CONNECTION+",链接成功");
                ct.countDown();
            }
        }else{
            String path = watchedEvent.getPath();
            switch (watchedEvent.getType()){
                case NodeCreated:
                    System.out.println("zookeeper服务："+ CONNECTION+",创建节点："+path+"--success");
                    break;
                case NodeDataChanged:
                    System.out.println("zookeeper服务："+ CONNECTION+",修改节点数据成功："+path+"--success");
                    break;
                case NodeChildrenChanged:
                    System.out.println("zookeeper服务："+ CONNECTION+",修改子节点数据成功："+path+"--success");
                    break;
                case NodeDeleted:
                    System.out.println("zookeeper服务："+ CONNECTION+"删除节点："+path+"--success");
                    break;
            }
        }
        System.out.println(watchedEvent.toString());
    }
}
