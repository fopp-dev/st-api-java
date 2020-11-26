package com.xc.dao;

import com.xc.pojo.SiteHksSetting;
import java.util.List;

public interface SiteHksSettingMapper {
    int deleteByPrimaryKey(Integer paramInteger);

    int insert(SiteHksSetting paramSiteSetting);

    int insertSelective(SiteHksSetting paramSiteSetting);

    SiteHksSetting selectByPrimaryKey(Integer paramInteger);

    int updateByPrimaryKeySelective(SiteHksSetting paramSiteSetting);

    int updateByPrimaryKey(SiteHksSetting paramSiteSetting);

    List findAllSiteHksSetting();
}
