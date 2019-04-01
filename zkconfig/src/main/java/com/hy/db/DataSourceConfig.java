package com.hy.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by VULCAN on 2018/10/11.
 */

@Configuration
public class DataSourceConfig {

    public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/world";
    public static final String USER_NAME = "root";
    public static final String PASSWORLD = "6126540";



    @Bean
    public DataSource dataSource() {
        EnjoyDataSource dataSource = new EnjoyDataSource();
        dataSource.setUrl(DEFAULT_URL);
        dataSource.setUsername(USER_NAME);
        dataSource.setPassword(PASSWORLD);
        dataSource.setDefaultReadOnly(false);

        return  dataSource;
    }
}
