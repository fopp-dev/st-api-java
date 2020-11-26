package com.xc.service.impl;

import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.dao.*;
import com.xc.pojo.*;
import com.xc.pojo.User;
import com.xc.service.ISitePriceSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iSitePriceSettingService")
public class SitePriceSettingServiceImpl implements ISitePriceSettingService {

    @Autowired
    SitePriceSettingMapper sitePriceSettingMapper;

    @Override
    public ServerResponse listByAdmin() {

        List<SitePriceSetting> sitePriceSettings = this.sitePriceSettingMapper.listByAdmin();

        PageInfo pageInfo = new PageInfo(sitePriceSettings);

        return ServerResponse.createBySuccess(pageInfo);
    }
}
