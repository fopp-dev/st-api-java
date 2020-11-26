package com.xc.service;


import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.pojo.User;

import java.math.BigDecimal;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {
  ServerResponse reg(String paramString1, String paramString2, String paramString3, String paramString4, HttpServletRequest paramHttpServletRequest);

  ServerResponse login(String paramString1, String paramString2, HttpServletRequest paramHttpServletRequest);

  User getCurrentUser(HttpServletRequest paramHttpServletRequest);

  User getCurrentRefreshUser(HttpServletRequest paramHttpServletRequest);

  ServerResponse addOption(String paramString,String paramString2, HttpServletRequest paramHttpServletRequest);

  ServerResponse delOption(String paramString, HttpServletRequest paramHttpServletRequest);

  ServerResponse isOption(String paramString, HttpServletRequest paramHttpServletRequest);

  ServerResponse getUserInfo(HttpServletRequest paramHttpServletRequest);

  ServerResponse updatePwd(String paramString1, String paramString2, HttpServletRequest paramHttpServletRequest);

  ServerResponse checkPhone(String paramString);

  ServerResponse updatePwd(String paramString1, String paramString2, String paramString3);

  ServerResponse update(User paramUser);

  ServerResponse auth(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, HttpServletRequest paramHttpServletRequest);

  ServerResponse transAmt(Integer paramInteger1, Integer paramInteger2, HttpServletRequest paramHttpServletRequest);

  void ForceSellTask();
  void ForceGgSellTask();
  void ForceSellMessageTask();
  void ForceGgSellMessageTask();

  void ForceSellIndexTask();
  void ForceSellIndexsMessageTask();

  void ForceSellFuturesTask();
  void ForceSellFuturesMessageTask();

  void qh1();

  void zs1();

  ServerResponse listByAgent(String paramString1, String paramString2, Integer paramInteger1, Integer paramInteger2, int paramInt1, int paramInt2, HttpServletRequest paramHttpServletRequest);
    void listExportByAdmin(String realName, String phone,  Integer agentId,  Integer accountType, Integer isActive, HttpServletResponse response);
  ServerResponse addSimulatedAccount(Integer paramInteger1, String paramString1, String paramString2, String paramString3, Integer paramInteger2, HttpServletRequest paramHttpServletRequest);

  ServerResponse<PageInfo> listByAdmin(String paramString1, String paramString2, Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, int paramInt1, int paramInt2, HttpServletRequest paramHttpServletRequest);

  ServerResponse findByUserId(Integer paramInteger);

  ServerResponse updateLock(Integer paramInteger);

  ServerResponse updateAmt(Integer paramInteger1, Integer paramInteger2, Integer paramInteger3, Integer paramInteger4);

  ServerResponse delete(Integer paramInteger, HttpServletRequest paramHttpServletRequest);

  int CountUserSize(Integer paramInteger);

  BigDecimal CountUserAmt(Integer paramInteger);

  BigDecimal CountEnableAmt(Integer paramInteger);

  ServerResponse authByAdmin(Integer paramInteger1, Integer paramInteger2, String paramString);

  ServerResponse findIdWithPwd(String phone);

  ServerResponse updateWithPwd(String paramString1, String paramString2);

  void updateUserAmt(Double amt, Integer user_id);


  ServerResponse capitalTransAmt(Integer amt, Integer type, HttpServletRequest request);

  ServerResponse capitalTransHmt(Integer amt, Integer type, HttpServletRequest request);

  ServerResponse abstractListByAdmin(String realName, String phone, Integer agentId, Integer accountType, Date startTime, Date endTime, int pageNum, int pageSize, HttpServletRequest request);

  ServerResponse emailReg(String agentCode, String email, String userPwd, HttpServletRequest httpServletRequest);


  ServerResponse emailLogin(String email, String userPwd, HttpServletRequest request);

  ServerResponse updateEmail(Integer userId,String email);

  ServerResponse changePWD(String email, String code, String passWord);

  ServerResponse capitalToHmt(Integer amt);

  ServerResponse hmtToCapital(Double amt);
}
