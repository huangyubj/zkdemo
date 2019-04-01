package com.hy.service.imp;

import com.hy.mybatis.entity.City;
import com.hy.mybatis.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CityService implements com.hy.service.CityService {

    @Autowired
    private CityMapper cityMapper;
    @Override
    public boolean addCity(City city) {
        int i = cityMapper.insert(city);
        return i > 0;
    }

    @Override
    public boolean deleteCity(int pk) {
        int i = cityMapper.deleteByPrimaryKey(pk);
        return i > 0;
    }

    @Override
    public City getCity(int pk) {
        City city = cityMapper.selectByPrimaryKey(pk);
        return city;
    }

    @Override
    public void batchAdd(List<City> citys) {

    }
}
