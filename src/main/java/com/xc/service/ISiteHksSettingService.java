package com.xc.service;


import com.xc.common.ServerResponse;
import com.xc.pojo.SiteHksSetting;

public interface ISiteHksSettingService {
    SiteHksSetting getSiteHksSetting();

    ServerResponse update(SiteHksSetting paramSiteSetting);
}