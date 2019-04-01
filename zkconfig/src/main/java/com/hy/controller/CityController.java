package com.hy.controller;

import com.hy.db.EnjoyDataSource;
import com.hy.listener.InitListener;
import com.hy.mybatis.entity.City;
import com.hy.mybatis.mapper.CityMapper;
import com.hy.util.RuntimeContext;
import com.hy.util.ZookeeperUtil;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RestController
public class CityController {

    @Resource
    private CityMapper cityMapper;
    @RequestMapping("city")
    public String cityInfo(){
        City city = cityMapper.selectByPrimaryKey(4088);
        return city.getDistrict();
    }

    @Resource
    private DataSource dataSource;
    private CuratorFramework curatorFramework = ZookeeperUtil.getCuratorFramework();

    @RequestMapping("/demo")
    public String demo() {

        EnjoyDataSource dataSource = (EnjoyDataSource) this.dataSource;
        try {
            if(dataSource.getUrl().endsWith("1")){
                curatorFramework.setData().forPath(InitListener.URL, "jdbc:mysql://localhost:3306/world".getBytes());
            }else{
                curatorFramework.setData().forPath(InitListener.URL, "jdbc:mysql://localhost:3306/world1".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        dataSource.changeDataSource();

        //EnjoyDataSource.prop.put(EnjoyDataSource.PROP_KEY_URL,"jdbc:mysql://localhost:3306/mysqldemo2");
        //EnjoyDataSource.changeDataSource();

        return "切换成功";
    }
}
