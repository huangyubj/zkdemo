package com.hy.listener;

import com.hy.db.EnjoyDataSource;
import com.hy.util.RuntimeContext;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InitListener implements ServletContextListener {

    private  static final String URL = "/db/url";
    private  static final String PASSWORD = "/db/password";
    private  static final String USERNAME = "/db/username";
    private  static final String DRIVER = "/db/driver";

    private CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("0.0.0.0:2181",
            60000, 1000, new ExponentialBackoffRetry(1000, 100));

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        init();
    }

    private void init() {
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
