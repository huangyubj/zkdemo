package com.hy.listener;

import com.hy.db.DataSourceConfig;
import com.hy.db.EnjoyDataSource;
import com.hy.util.RuntimeContext;
import com.hy.util.ZookeeperUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitListener implements ServletContextListener {

    public  static final String URL = "/db/url";
    public  static final String PASSWORD = "/db/password";
    public  static final String USERNAME = "/db/username";
    public  static final String DRIVER = "/db/driver";

    private CuratorFramework curatorFramework = ZookeeperUtil.getCuratorFramework();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        init();
    }

    private void init() {
        curatorFramework.start();
        try {
            createNode(URL, DataSourceConfig.DEFAULT_URL);
            createNode(PASSWORD, DataSourceConfig.USER_NAME);
            createNode(USERNAME, DataSourceConfig.PASSWORLD);
            createNode(DRIVER, DataSourceConfig.DEFAULT_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TreeCache treeCache = new TreeCache(curatorFramework, "/db");
        ExecutorService service = Executors.newFixedThreadPool(2);
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
                        updateDatasource(treeCacheEvent.getData().getPath(), new String(treeCacheEvent.getData().getData()));
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
        }, service);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void createNode(String path, String data){
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            if(stat == null){
                curatorFramework.create().creatingParentsIfNeeded().forPath(path, data.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateDatasource(String path, String data){
        EnjoyDataSource dataSource = RuntimeContext.getBean(EnjoyDataSource.class);
        switch (path){
            case URL:
                dataSource.setUrl(data);
                break;
            case PASSWORD:
                dataSource.setUrl(data);
                break;
            case USERNAME:
                dataSource.setUrl(data);
                break;
            case DRIVER:
                dataSource.setUrl(data);
                break;
        }
        dataSource.changeDataSource();
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
