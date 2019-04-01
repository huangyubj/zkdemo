package zkclien;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;

import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

public class ZkClienTest {

    private final String CONNECTION = "127.0.0.1:2181";
    private final int TIME_OUT = 3000;
    private ZkClient zk;
    private final static String path = "/hydata";

    public static void main(String[] args) throws Exception {
        ZkClienTest zkClienTest = new ZkClienTest();
        zkClienTest.init();
        zkClienTest.createParentNode(path, "testhydata");
        zkClienTest.getData(path);
        zkClienTest.updateData(path, "12312312");
        zkClienTest.deleteData(path);
    }

    public void init(){
        zk = new ZkClient(CONNECTION, TIME_OUT);
        zk.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {
                if(keeperState == SyncConnected){
                    System.out.println("zookeeper服务："+ CONNECTION+",链接成功");
                }
                System.out.println("zookeeper服务："+ keeperState);
            }

            @Override
            public void handleNewSession() throws Exception {
                System.out.println("zookeeper服务："+ CONNECTION+",handleNewSession");
            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {
                System.out.println("zookeeper服务："+ CONNECTION+",handleSessionEstablishmentError");
            }
        });
        zk.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("zookeeper服务："+ CONNECTION+"修改节点数据成功："+s +"----0:"+o.toString()+"--success");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("zookeeper服务："+ CONNECTION+"删除节点："+s+"--success");
            }
        });
    }
    public void createParentNode(String path, String data) throws KeeperException, InterruptedException {
        String createPath = zk.create(path, data, CreateMode.PERSISTENT);
        System.out.println("createParentNode----createPath:"+createPath);
    }

    public void updateData(String path, String data) throws KeeperException, InterruptedException {
        if(zk.exists(path)){
            zk.writeData(path,data);
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }

    public void deleteData(String path) throws KeeperException, InterruptedException {
        if(zk.exists(path)){
            zk.delete(path, -1);
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }
    public void getData(String path) throws KeeperException, InterruptedException {
        String data = zk.readData(path);
        System.out.println("获取节点的数据，"+path+"---data:"+data);
    }
}
