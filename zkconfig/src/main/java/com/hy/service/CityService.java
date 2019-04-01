package com.hy.service;

import com.hy.mybatis.entity.City;

import java.util.List;

public interface CityService {
    boolean addCity(City city);
    boolean deleteCity(int pk);
    City getCity(int pk);
    void batchAdd(List<City> citys);
}
