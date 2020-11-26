package com.xc.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xc.dao.*;
import com.xc.pojo.*;
import com.xc.service.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.service.*;
import com.xc.utils.AdminThreadLocal;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.KeyUtils;
import com.xc.utils.email.SendHTMLMail;
import com.xc.utils.redis.RedisShardedPoolUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("iUserRechargeService")
public class UserRechargeServiceImpl implements IUserRechargeService {
    private static final Logger log = LoggerFactory.getLogger(UserRechargeServiceImpl.class);

    @Autowired
    UserRechargeMapper userRechargeMapper;

    @Autowired
    IUserService iUserService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IAgentUserService iAgentUserService;
    @Autowired
    AgentUserMapper agentUserMapper;
    @Autowired
    ISiteSettingService iSiteSettingService;
    @Autowired
    UserCashDetailMapper userCashDetailMapper;
    @Autowired
    ISiteInfoService iSiteInfoService;
    @Autowired
    SitePayMapper sitePayMapper;

    public ServerResponse checkInMoney(int maxOrder, Integer userId) {
        int count = this.userRechargeMapper.checkInMoney(0, userId);

        if (count > maxOrder) {
            return ServerResponse.createByErrorMsg("一小时内只能发起" + maxOrder + "次入金");
        }
        return ServerResponse.createBySuccess();
    }

    public void listExportByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletResponse response) {

        Timestamp begin_time = null;
        if (StringUtils.isNotBlank(beginTime)) {
            begin_time = DateTimeUtil.searchStrToTimestamp(beginTime);
        }
        Timestamp end_time = null;
        if (StringUtils.isNotBlank(endTime)) {
            end_time = DateTimeUtil.searchStrToTimestamp(endTime);
        }


        List<UserRecharge> userRecharges = this.userRechargeMapper.listByAdmin(agentId, userId, realName, state, begin_time, end_time);

        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=recharge.xlsx");
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "充值列表").head(UserRecharge.class).build();
            excelWriter.write(userRecharges, writeSheet1);
            excelWriter.finish();
        } catch (Exception var10) {
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + var10.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
            var10.printStackTrace();
        } finally {
        }

    }
    public ServerResponse inMoney(String amt, String payType,Integer payId,String vouImage, HttpServletRequest request) {
        if (StringUtils.isBlank(amt) || StringUtils.isBlank(payType)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }
        if (StringUtils.isBlank(vouImage)) {
            return ServerResponse.createByErrorMsg("请上传凭证");
        }
        SiteSetting siteSetting = this.iSiteSettingService.getSiteSetting();
        if (siteSetting == null) {
            return ServerResponse.createByErrorMsg("设置set未初始化");
        }
        if ((new BigDecimal(siteSetting.getChargeMinAmt() + "")).compareTo(new BigDecimal(amt)) == 1) {
            return ServerResponse.createByErrorMsg("充值金额不得低于" + siteSetting.getChargeMinAmt() + "元");
        }


        SiteInfo siteInfo = null;
        ServerResponse serverResponseInfo = this.iSiteInfoService.getInfo();
        if (serverResponseInfo.isSuccess()) {
            siteInfo = (SiteInfo) serverResponseInfo.getData();
            if (StringUtils.isBlank(siteInfo.getSiteHost()) ||
                    StringUtils.isBlank(siteInfo.getSiteEmailTo())) {
                return ServerResponse.createByErrorMsg("请先设置Host and ToEmail");
            }
        } else {
            return serverResponseInfo;
        }

        User user = this.iUserService.getCurrentRefreshUser(request);
        if (user.getIsActive().intValue() != 2) {
            return ServerResponse.createByErrorMsg("未实名认证不能发起充值");
        }


        ServerResponse serverResponse = checkInMoney(10, user.getId());
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }


        UserRecharge userRecharge = new UserRecharge();

        userRecharge.setUserId(user.getId());
        userRecharge.setNickName(user.getRealName());
        userRecharge.setAgentId(user.getAgentId());

        String ordersn = KeyUtils.getRechargeOrderSn();
        userRecharge.setOrderSn(ordersn);

        userRecharge.setPayChannel(payType);
        userRecharge.setPayAmt(new BigDecimal(amt));
        userRecharge.setOrderStatus(Integer.valueOf(0));
        userRecharge.setAddTime(new Date());
        userRecharge.setPayId(payId);
        userRecharge.setVouImage(vouImage);


        int insertCount = this.userRechargeMapper.insertSelective(userRecharge);
        if (insertCount > 0) {

            String email_token = KeyUtils.getUniqueKey();

            String redisSetExResult = RedisShardedPoolUtils.setEx(email_token, email_token, 300);

            log.info("用户充值，保存redis token成功，redisSetExResult = {}", redisSetExResult);

            SendHTMLMail.send(user, userRecharge, email_token, siteInfo
                    .getSiteHost(), siteInfo.getSiteEmailTo());
            log.info("用户充值，发送审核邮件成功");
            return ServerResponse.createBySuccessMsg("创建支付订单成功！");
        }
        return ServerResponse.createByErrorMsg("创建支付订单失败");
    }


    public ServerResponse findUserRechargeByOrderSn(String orderSn) {
        UserRecharge userRecharge = this.userRechargeMapper.findUserRechargeByOrderSn(orderSn);
        if (userRecharge != null) {
            return ServerResponse.createBySuccess(userRecharge);
        }
        return ServerResponse.createByErrorMsg("找不到充值订单");
    }


    @Transactional
    public ServerResponse chargeSuccess(UserRecharge userRecharge) throws Exception {
        log.info("充值订单 确认成功操作 id = {}", userRecharge.getId());

        if (userRecharge.getOrderStatus().intValue() != 0) {
            return ServerResponse.createByErrorMsg("订单状态不能重复修改");
        }


        User user = this.userMapper.selectByPrimaryKey(userRecharge.getUserId());
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }
//        BigDecimal userAmt_before = user.getUserAmt();
//        BigDecimal enableAmt_before = user.getEnableAmt();
//        user.setUserAmt(userAmt_before.add(userRecharge.getPayAmt()));
//        user.setEnableAmt(enableAmt_before.add(userRecharge.getPayAmt()));
        //liuqi 充值改本金
        BigDecimal userCapitalBefore = user.getUserCapital();
        user.setUserCapital(userCapitalBefore.add(userRecharge.getPayAmt()));
        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            log.info("1.修改用户资金成功");
        } else {
            return ServerResponse.createByErrorMsg("失败，修改用户资金失败");
        }


        userRecharge.setOrderStatus(Integer.valueOf(1));
        userRecharge.setPayTime(new Date());
        int updateCCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCCount > 0) {
            log.info("2.修改订单状态成功");
        } else {
            throw new Exception("2. 修改订单状态失败!");
        }


        UserCashDetail ucd = new UserCashDetail();
        ucd.setAgentId(user.getAgentId());
        ucd.setAgentName(user.getAgentName());
        ucd.setUserId(user.getId());
        ucd.setUserName(user.getRealName());
        ucd.setDeType("用户充值");
        ucd.setDeAmt(userRecharge.getPayAmt());
        ucd.setDeSummary("用户充值成功，充值前总金额:" + userCapitalBefore + ",充值后总金额:" + user.getUserCapital());

        ucd.setAddTime(new Date());
        ucd.setIsRead(Integer.valueOf(0));
        int insertCount = this.userCashDetailMapper.insert(ucd);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("充值成功！");
        }
        return ServerResponse.createByErrorMsg("充值失败");
    }


    public ServerResponse chargeFail(UserRecharge userRecharge) throws Exception {
        if (userRecharge.getOrderStatus().intValue() != 0) {
            return ServerResponse.createByErrorMsg("订单状态不能重复修改");
        }

        userRecharge.setOrderStatus(Integer.valueOf(2));
        int updateCCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCCount > 0) {
            return ServerResponse.createBySuccessMsg("订单已修改为失败");
        }
        return ServerResponse.createByErrorMsg("修改出现异常");
    }


    public ServerResponse chargeCancel(UserRecharge userRecharge) throws Exception {
        if (userRecharge.getOrderStatus().intValue() != 0) {
            return ServerResponse.createByErrorMsg("订单状态不能重复修改");
        }

        userRecharge.setOrderStatus(Integer.valueOf(3));
        int updateCCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCCount > 0) {
            return ServerResponse.createBySuccessMsg("订单取消成功");
        }
        return ServerResponse.createByErrorMsg("订单取消出现异常");
    }


    public ServerResponse<PageInfo> findUserChargeList(String payChannel, String orderStatus, HttpServletRequest request, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        User user = this.iUserService.getCurrentUser(request);

        List<UserRecharge> userRecharges = this.userRechargeMapper.findUserChargeList(user.getId(), payChannel, orderStatus);

        SiteAdmin siteAdmin = null;

        userRecharges.stream().forEach(userRecharge -> {

            userRecharge.setSiteAdmin(siteAdmin);
//            return userRecharge;
        });

        PageInfo pageInfo = new PageInfo(userRecharges);

        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse<PageInfo> listByAgent(Integer agentId, String realName, String payChannel, Integer state, HttpServletRequest request, int pageNum, int pageSize) {
        AgentUser currentAgent = this.iAgentUserService.getCurrentAgent(request);


        if (agentId != null) {
            AgentUser agentUser = this.agentUserMapper.selectByPrimaryKey(agentId);
            if (agentUser.getParentId() != currentAgent.getId()) {
                return ServerResponse.createByErrorMsg("不能查询非下级代理记录");
            }
        }
        Integer searchId = null;
        if (agentId == null) {
            searchId = currentAgent.getId();
        } else {
            searchId = agentId;
        }


        PageHelper.startPage(pageNum, pageSize);


        List<UserRecharge> userRecharges = this.userRechargeMapper.listByAgent(searchId, realName, payChannel, state);

        PageInfo pageInfo = new PageInfo(userRecharges);

        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse listByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletRequest request, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);


        Timestamp begin_time = null;
        if (StringUtils.isNotBlank(beginTime)) {
            begin_time = DateTimeUtil.searchStrToTimestamp(beginTime);
        }
        Timestamp end_time = null;
        if (StringUtils.isNotBlank(endTime)) {
            end_time = DateTimeUtil.searchStrToTimestamp(endTime);
        }


        List<UserRecharge> userRecharges = this.userRechargeMapper.listByAdmin(agentId, userId, realName, state, begin_time, end_time);

        PageInfo pageInfo = new PageInfo(userRecharges);

        return ServerResponse.createBySuccess(pageInfo);
    }


    @Transactional
    public ServerResponse updateState(Integer chargeId, Integer state, String vouImage, String orderDesc ) throws Exception {
        UserRecharge userRecharge = this.userRechargeMapper.selectByPrimaryKey(chargeId);

        if (userRecharge == null) {
            return ServerResponse.createByErrorMsg("充值订单不存在");
        }
        if (userRecharge.getOrderStatus().intValue() != 0) {
            return ServerResponse.createByErrorMsg("订单状态不是下单状态不能更改");
        }


        if (state.intValue() == 1) {

            User user = this.userMapper.selectByPrimaryKey(userRecharge.getUserId());
            if (user == null) {
                return ServerResponse.createByErrorMsg("用户不存在");
            }
//            BigDecimal user_amt = user.getUserAmt().add(userRecharge.getPayAmt());
//            log.info("管理员确认订单成功，增加用户 {} 总资金，原金额 = {} , 增加后 = {}", new Object[]{user.getId(), user.getUserAmt(), user_amt});
//            user.setUserAmt(user_amt);
//            BigDecimal user_enable_amt = user.getEnableAmt().add(userRecharge.getPayAmt());
//            log.info("管理员确认订单成功，增加用户 {} 可用资金，原金额 = {} , 增加后 = {}", new Object[]{user.getId(), user.getEnableAmt(), user_enable_amt});
//            user.setEnableAmt(user_enable_amt);

            //  0 本金 1 A股票 2 港股
            switch(userRecharge.getType().intValue()){
                case 0 :
                    user.setUserCapital(user.getUserCapital().add(userRecharge.getPayAmt()));
                    break;
                case 1 :
                    user.setUserAmt(user.getUserAmt().add(userRecharge.getPayAmt()));
                    user.setEnableAmt(user.getEnableAmt().add(userRecharge.getPayAmt()));
                    break;
                case 2 :
                    user.setUserHmt(user.getUserHmt().add(userRecharge.getPayAmt()));
                    user.setEnableHmt(user.getEnableHmt().add(userRecharge.getPayAmt()));
                    break;
                default :

            }

//            BigDecimal userCapital = user.getUserCapital().add(userRecharge.getPayAmt());
//            log.info("管理员确认订单成功，增加用户 {} 本金，原金额 = {} , 增加后 = {}", new Object[]{user.getId(), user.getUserCapital(), userCapital});
//            user.setUserCapital(userCapital);

            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                log.info("修改用户资金成功！");
                //修改支付通道累积金额
                SitePay sitePay = sitePayMapper.selectByPrimaryKey(userRecharge.getPayId());
                if(sitePay != null) {
                    BigDecimal total = userRecharge.getPayAmt().add(sitePay.getTotalPrice());
                    sitePay.setTotalPrice(total);
                    sitePayMapper.updateByPrimaryKeySelective(sitePay);
                }
            } else {
                log.error("修改用户资金出错，抛出异常");
                throw new Exception("修改用户资金出错，抛出异常");
            }
        }


        userRecharge.setOrderStatus(Integer.valueOf((state.intValue() == 1) ? 1 : 2));


        userRecharge.setVouImage(vouImage);


        userRecharge.setOrderDesc(orderDesc);


        Integer adminId = AdminThreadLocal.get().getId();

        userRecharge.setAdminId(adminId);

        userRecharge.setPayTime(new Date());
        int updateCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改订单状态成功！");
        }
        return ServerResponse.createByErrorMsg("修改订单状态失败！");
    }

    @Transactional
    public ServerResponse updateDesc(Integer chargeId, String orderDesc)  {
        UserRecharge userRecharge = this.userRechargeMapper.selectByPrimaryKey(chargeId);

        if (userRecharge == null) {
            return ServerResponse.createByErrorMsg("充值订单不存在");
        }

        userRecharge.setOrderDesc(orderDesc);

        Integer adminId = AdminThreadLocal.get().getId();

        userRecharge.setAdminId(adminId);

        int updateCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改订单备注成功！");
        }
        return ServerResponse.createByErrorMsg("修改订单备注失败！");
    }

    @Transactional
    public ServerResponse updateAdminDesc(Integer chargeId, String orderAdminDesc)  {
        UserRecharge userRecharge = this.userRechargeMapper.selectByPrimaryKey(chargeId);

        if (userRecharge == null) {
            return ServerResponse.createByErrorMsg("充值订单不存在");
        }

        userRecharge.setOrderAdminDesc(orderAdminDesc);

        Integer adminId = AdminThreadLocal.get().getId();

        userRecharge.setAdminId(adminId);

        int updateCount = this.userRechargeMapper.updateByPrimaryKeySelective(userRecharge);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改后端订单备注成功！");
        }
        return ServerResponse.createByErrorMsg("修改订单备注失败！");
    }


    public ServerResponse createOrder(Integer userId, Integer state, Integer amt, String payChannel, String orderDesc, Integer type) {
        if (userId == null || state == null || amt == null || type == null) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }

        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMsg("找不到用户");
        }

        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUserId(user.getId());
        userRecharge.setNickName(user.getRealName());
        userRecharge.setAgentId(user.getAgentId());

        String ordersn = KeyUtils.getRechargeOrderSn();
        userRecharge.setOrderSn(ordersn);

        userRecharge.setPayChannel(payChannel);
        userRecharge.setPayAmt(new BigDecimal(amt.intValue()));
        userRecharge.setAddTime(new Date());
        userRecharge.setPayTime(new Date());
        userRecharge.setOrderDesc(orderDesc);

        if (state.intValue() == 0) {
            userRecharge.setOrderStatus(Integer.valueOf(0));
        } else if (state.intValue() == 1) {
            userRecharge.setOrderSn(ordersn);
            userRecharge.setPayChannel(payChannel);
            userRecharge.setOrderStatus(Integer.valueOf(1));

//            user.setUserAmt(user.getUserAmt().add(new BigDecimal(amt.intValue())));
//            user.setEnableAmt(user.getEnableAmt().add(new BigDecimal(amt.intValue())));
            //liuqi 0923 修改添加本金

            //  0 本金 1 A股票 2 港股
            switch(type.intValue()){
                case 0 :
                    user.setUserCapital(user.getUserCapital().add(new BigDecimal(amt.intValue())));
                    break;
                case 1 :
                    user.setUserAmt(user.getUserAmt().add(new BigDecimal(amt.intValue())));
                    user.setEnableAmt(user.getEnableAmt().add(new BigDecimal(amt.intValue())));
                    break;
                case 2 :
                    user.setUserHmt(user.getUserHmt().add(new BigDecimal(amt.intValue())));
                    user.setEnableHmt(user.getEnableHmt().add(new BigDecimal(amt.intValue())));
                    break;
                default :

            }
            this.userMapper.updateByPrimaryKeySelective(user);
        } else if (state.intValue() == 2) {
            userRecharge.setOrderStatus(Integer.valueOf(2));
        } else {
            return ServerResponse.createByErrorMsg("订单状态不正确");
        }

        userRecharge.setType(type);

        Integer adminId = AdminThreadLocal.get().getId();

        userRecharge.setAdminId(adminId);

        int insertCount = this.userRechargeMapper.insert(userRecharge);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("生成订单成功！");
        }
        return ServerResponse.createByErrorMsg("生成订单失败，请重试");
    }


    public ServerResponse del(Integer cId) {
        if (cId == null) {
            return ServerResponse.createByErrorMsg("id不能为空");
        }
        int updateCount = this.userRechargeMapper.deleteByPrimaryKey(cId);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("删除成功");
        }
        return ServerResponse.createByErrorMsg("删除失败");
    }


    public int deleteByUserId(Integer userId) {
        return this.userRechargeMapper.deleteByUserId(userId);
    }


    public BigDecimal CountChargeSumAmt(Integer chargeState) {
        return this.userRechargeMapper.CountChargeSumAmt(chargeState);
    }

    public BigDecimal CountTotalRechargeAmountByTime(Integer chargeState) {
        return this.userRechargeMapper.CountTotalRechargeAmountByTime(chargeState);
    }
}
