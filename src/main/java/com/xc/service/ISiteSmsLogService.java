package com.xc.service;


import com.xc.common.ServerResponse;
import com.xc.pojo.SiteSmsLog;

public interface ISiteSmsLogService {
  ServerResponse smsList(String paramString, int paramInt1, int paramInt2);

  void addData(SiteSmsLog siteSmsLog);

}
