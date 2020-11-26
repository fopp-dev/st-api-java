package com.xc.service;


import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.pojo.UserRecharge;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserRechargeService {
  ServerResponse checkInMoney(int paramInt, Integer paramInteger);
  
  ServerResponse inMoney(String paramString1, String paramString2,Integer payId,String vouImage, HttpServletRequest paramHttpServletRequest);
  
  ServerResponse findUserRechargeByOrderSn(String paramString);
  
  ServerResponse chargeSuccess(UserRecharge paramUserRecharge) throws Exception;
  
  ServerResponse chargeFail(UserRecharge paramUserRecharge) throws Exception;
  
  ServerResponse chargeCancel(UserRecharge paramUserRecharge) throws Exception;
  
  ServerResponse<PageInfo> findUserChargeList(String paramString1, String paramString2, HttpServletRequest paramHttpServletRequest, int paramInt1, int paramInt2);
  
  ServerResponse<PageInfo> listByAgent(Integer paramInteger1, String paramString1, String paramString2, Integer paramInteger2, HttpServletRequest paramHttpServletRequest, int paramInt1, int paramInt2);
  
  ServerResponse listByAdmin(Integer paramInteger1, Integer paramInteger2, String paramString1, Integer paramInteger3, String paramString2, String paramString3, HttpServletRequest paramHttpServletRequest, int paramInt1, int paramInt2);
  void listExportByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletResponse response);
  ServerResponse updateState(Integer paramInteger1, Integer paramInteger2, String paramString3, String paramString4) throws Exception;
  
  ServerResponse createOrder(Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, String paramString, String orderDesc, Integer type);
  
  ServerResponse del(Integer paramInteger);
  
  int deleteByUserId(Integer paramInteger);
  
  BigDecimal CountChargeSumAmt(Integer paramInteger);

  BigDecimal CountTotalRechargeAmountByTime(Integer paramInteger);

  ServerResponse updateDesc(Integer chargeId, String orderDesc);

  ServerResponse updateAdminDesc(Integer chargeId, String orderAdminDesc);
}
