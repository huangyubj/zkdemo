package curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuratorTest {

    private final String CONNECTION = "127.0.0.1:2181";
    private final int TIME_OUT = 30;
    private CuratorFramework curatorFramework;

    public static void main(String[] args) throws Exception {
        String path = "/hydata";
        CuratorTest curatorTest = new CuratorTest();
        curatorTest.init();
        curatorTest.createParentNode(path, "testhydata");
        curatorTest.createParentNode(path+"/child1", "child1");
        curatorTest.createParentNode(path+"/child2", "child2");
        curatorTest.getData(path);
        curatorTest.updateData(path, "12312312");
        curatorTest.deleteData(path);
    }
    public void init() throws Exception {
        curatorFramework = CuratorFrameworkFactory.newClient(CONNECTION, TIME_OUT, TIME_OUT, new ExponentialBackoffRetry(1000, 10));
        curatorFramework.start();
        /**
         * 三种watcher来做节点的监听
         * pathcache   监视一个路径下子节点的创建、删除、节点数据更新
         * NodeCache   监视一个节点的创建、更新、删除
         * TreeCache   pathcaceh+nodecache 的合体（监视路径下的创建、更新、删除事件），
         * 缓存路径下的所有子节点的数据
         */
        /**
         * 在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
         */
        ExecutorService pool = Executors.newFixedThreadPool(2);
//        final NodeCache nodeCache = new NodeCache(curatorFramework, "/hydata", false);
//        nodeCache.getListenable().addListener(new NodeCacheListener() {
//            @Override
//            public void nodeChanged() throws Exception {
//                System.out.println(nodeCache.getCurrentData().getStat().toString());;
//                System.out.println("数据变更后的结果："+new String(nodeCache.getCurrentData().getData()));;
//            }
//        },pool);
////        nodeCache.getListenable().addListener(() ->{
////            System.out.println("数据变更后的结果："+new String(nodeCache.getCurrentData().getData()));;
////        });
//        nodeCache.start();
        TreeCache treeCache = new TreeCache(curatorFramework, "/hydata");
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent treeCacheEvent) throws Exception {
                switch(treeCacheEvent.getType()){
                    case NODE_ADDED:
                        System.out.println("NODE_ADDED-->"+treeCacheEvent.toString());
                        break;
                    case NODE_REMOVED:
                        System.out.println("NODE_REMOVED-->"+treeCacheEvent.toString());
                        break;
                    case NODE_UPDATED:
                        System.out.println("NODE_UPDATED-->"+treeCacheEvent.toString());
                        break;
                    case CONNECTION_SUSPENDED:
                        System.out.println("CONNECTION_SUSPENDED-->"+treeCacheEvent.toString());
                        break;
                    case CONNECTION_LOST:
                        System.out.println("CONNECTION_LOST-->"+treeCacheEvent.toString());
                        break;
                    case INITIALIZED:
                        System.out.println("INITIALIZED-->"+treeCacheEvent.toString());
                        break;
                    case CONNECTION_RECONNECTED:
                        System.out.println("CONNECTION_RECONNECTED-->"+treeCacheEvent.toString());
                        break;
                }
                System.out.println("数据data："+ new String(treeCacheEvent.getData().getData()));
            }
        }, pool);
        treeCache.start();
    }

    public void createParentNode(String path, String data) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if(stat != null){
            System.out.println("createParentNode----"+stat.toString() );
        }else{
            curatorFramework.create().forPath(path, data.getBytes());
        }
    }

    public void updateData(String path, String data) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if(stat != null){
            curatorFramework.setData().forPath(path, data.getBytes());
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }

    public void deleteData(String path) throws Exception {
        Stat stat = curatorFramework.checkExists().forPath(path);
        if(stat != null){
            curatorFramework.delete().deletingChildrenIfNeeded().forPath(path);
        }else{
            System.out.println("修改节点错误，"+path+"不存在");
        }
    }
    public void getData(String path) throws Exception {
        byte[] bytes = curatorFramework.getData().forPath(path);
        String data = new String(bytes);
        System.out.println("获取节点的数据，"+path+"---data:"+data);
    }
}
