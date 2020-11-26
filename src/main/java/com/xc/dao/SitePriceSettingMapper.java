package com.xc.dao;

import com.xc.pojo.SitePriceSetting;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SitePriceSettingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SitePriceSetting record);

    int insertSelective(SitePriceSetting record);

    SitePriceSetting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SitePriceSetting record);

    int updateByPrimaryKey(SitePriceSetting record);

    List listByAdmin();
}