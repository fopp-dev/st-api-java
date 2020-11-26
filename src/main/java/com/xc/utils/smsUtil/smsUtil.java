package com.xc.utils.smsUtil;

import com.xc.common.ServerResponse;
import com.xc.controller.SmsApiController;
import com.xc.dao.SiteSmsLogMapper;
import com.xc.pojo.SiteSmsLog;
import com.xc.service.ISiteSmsLogService;
import com.xc.service.impl.SiteSmsLogServiceImpl;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.pay.CmcPayOuterRequestUtil;
import com.xc.utils.redis.RedisShardedPoolUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class smsUtil {
    private static final Logger log = LoggerFactory.getLogger(SmsApiController.class);

    public String sendSMS(String telephone) {
        String code = RandomStringUtils.randomNumeric(4);
        CmcPayOuterRequestUtil requestUtil = new CmcPayOuterRequestUtil();
        /*【高盛商赢】*/
        String uid = PropertiesUtil.getProperty("wj.sms.uid");
        String key = PropertiesUtil.getProperty("wj.sms.key");
        String coding = PropertiesUtil.getProperty("wj.sms.coding");
        String smscontent = "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！";
        try {
            uid = URLEncoder.encode(uid,"UTF-8");
            smscontent = URLEncoder.encode(smscontent,"UTF-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String url = "http://"+ coding +".api.smschinese.cn/?Uid="+ uid +"&Key="+ key +"&smsMob=" + telephone + "&smsText="+smscontent;
        log.info("smsurl"+url);
        String result = requestUtil.sendGet(url);
        log.info("smsresult="+result+"==code="+code);
        if (Integer.valueOf(result) < 0) {
            return "";
        } else {
            String keys = "AliyunSmsCode:" + telephone;
            RedisShardedPoolUtils.setEx(keys, code, 5400);
            return code;
        }
    }

}
