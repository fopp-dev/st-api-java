package com.xc.dao;

import com.xc.pojo.HkDollarRate;

import java.util.List;

public interface HkDollarRateMapper {

    int deleteByPrimaryKey(Integer paramInteger);

    int insert(HkDollarRate paramHkDollarRate);

    int insertSelective(HkDollarRate paramHkDollarRate);

    HkDollarRate selectByPrimaryKey(Integer paramInteger);

    int updateByPrimaryKeySelective(HkDollarRate paramHkDollarRate);

    int updateByPrimaryKey(HkDollarRate paramHkDollarRate);

    List findAllHkDollarRate();

}
