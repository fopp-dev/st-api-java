package com.xc.service.impl;


import com.xc.common.ServerResponse;

import com.xc.dao.SiteHksSettingMapper;

import com.xc.pojo.SiteHksSetting;

import com.xc.service.ISiteHksSettingService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


@Service("iSiteHksSettingService")
public class SiteHksSettingServiceImpl implements ISiteHksSettingService {

    @Autowired
    SiteHksSettingMapper siteHksSettingMapper;


    public SiteHksSetting getSiteHksSetting() {

        SiteHksSetting siteHksSetting = null;

        List list = this.siteHksSettingMapper.findAllSiteHksSetting();

        if (list.size() > 0) {

            siteHksSetting = (SiteHksSetting) list.get(0);

        }
        return siteHksSetting;
    }


    public ServerResponse update(SiteHksSetting setting) {
        if (setting.getId() == null) {
            return ServerResponse.createByErrorMsg("ID 不能为空");
        }
        SiteHksSetting siteHksSetting = this.siteHksSettingMapper.selectByPrimaryKey(setting.getId());
        if (siteHksSetting == null) {
            return ServerResponse.createByErrorMsg("查不到设置记录");
        }

        int updateCount = this.siteHksSettingMapper.updateByPrimaryKeySelective(setting);

        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");

    }

}
