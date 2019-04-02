package com.hy.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by VULCAN on 2018/10/11.
 */

@Configuration
public class DataSourceConfig {
    



    @Bean
    public DataSource dataSource() {
        EnjoyDataSource dataSource = new EnjoyDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/mysqldemo");
        dataSource.setUsername("root");
        dataSource.setPassword("6126540");
        dataSource.setDefaultReadOnly(false);

        return  dataSource;
    }
}
