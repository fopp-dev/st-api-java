package com.xc.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.dao.SiteSmsLogMapper;
import com.xc.pojo.SiteSmsLog;
import com.xc.service.ISiteSmsLogService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("iSiteSmsLogService")
public class SiteSmsLogServiceImpl implements ISiteSmsLogService {
    @Autowired
    SiteSmsLogMapper siteSmsLogMapper;

    public ServerResponse smsList(String phoneNum, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List smslist = this.siteSmsLogMapper.smsList(phoneNum);
        PageInfo pageInfo = new PageInfo(smslist);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public void addData(SiteSmsLog siteSmsLog) {
        siteSmsLogMapper.insert(siteSmsLog);
    }

}