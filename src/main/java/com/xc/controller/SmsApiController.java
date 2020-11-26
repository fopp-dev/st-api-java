package com.xc.controller;


import com.xc.common.ServerResponse;

import com.xc.pojo.SiteSmsLog;
import com.xc.service.ISiteSmsLogService;
import com.xc.service.ISmsService;

import com.xc.utils.DateTimeUtil;
import com.xc.utils.smsUtil.smsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping({"/api/sms/"})
public class SmsApiController {
    private static final Logger log = LoggerFactory.getLogger(SmsApiController.class);

    @Autowired
    ISmsService iSmsService;

    @Autowired
    ISiteSmsLogService iSiteSmsLogService;

    //注册用户 短信发送
    @RequestMapping({"sendRegSms.do"})
    @ResponseBody
    public ServerResponse sendRegSms(String phoneNum) {
        if (StringUtils.isBlank(phoneNum)) {
            return ServerResponse.createByErrorMsg("发送失败，手机号不能为空");
        }
        smsUtil smsUtil = new smsUtil();
        log.info("smsphone"+phoneNum);
        String code = smsUtil.sendSMS(phoneNum);
        if (!StringUtils.isEmpty(code)) {
            SiteSmsLog siteSmsLog = new SiteSmsLog();
            siteSmsLog.setSmsPhone(phoneNum);
            siteSmsLog.setSmsTitle("注册验证码");
            siteSmsLog.setSmsCnt(code);
            siteSmsLog.setSmsStatus(Integer.valueOf(0));
            siteSmsLog.setSmsTemplate("字段无用");
            siteSmsLog.setAddTime(DateTimeUtil.getCurrentDate());
            iSiteSmsLogService.addData(siteSmsLog);
            return ServerResponse.createBySuccessMsg("发送成功");
        } else {
            return ServerResponse.createByErrorMsg("短信发送失败，请重试");
        }
    }

    //找回密码 短信发送
    @RequestMapping({"sendForgetSms.do"})
    @ResponseBody
    public ServerResponse sendForgetSms(String phoneNum) {
        return this.iSmsService.sendAliyunSMS(phoneNum, "SMS_174915941");
    }
}
