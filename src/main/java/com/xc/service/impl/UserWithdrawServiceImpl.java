package com.xc.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xc.pojo.*;
import com.xc.service.*;
import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;

import com.xc.common.ServerResponse;

import com.xc.dao.AgentUserMapper;

import com.xc.dao.UserMapper;

import com.xc.dao.UserWithdrawMapper;

import com.xc.service.*;
import com.xc.utils.AdminThreadLocal;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.stock.WithDrawUtils;

import java.io.IOException;
import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;


@Service("iUserWithdrawService")
public class UserWithdrawServiceImpl implements IUserWithdrawService {

    private static final Logger log = LoggerFactory.getLogger(UserWithdrawServiceImpl.class);


    @Autowired
    UserWithdrawMapper userWithdrawMapper;


    @Autowired
    IUserService iUserService;


    @Autowired
    UserMapper userMapper;


    @Autowired
    IAgentUserService iAgentUserService;

    @Autowired
    AgentUserMapper agentUserMapper;

    @Autowired
    IUserPositionService iUserPositionService;

    @Autowired
    IUserBankService iUserBankService;

    @Autowired
    ISiteSettingService iSiteSettingService;

    public void listExportByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletResponse response) {

        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.listByAdmin(agentId, userId, realName, state, beginTime, endTime);
        List<UserWithdrawExcel> userWithdrawExcels = new ArrayList<>();

        for (UserWithdraw userWithdraw: userWithdraws){
            UserWithdrawExcel ex = new UserWithdrawExcel();
            BeanUtils.copyProperties(userWithdraw,ex);
            ex.setNeedAmt(userWithdraw.getWithAmt().subtract(userWithdraw.getWithFee()));
            switch (userWithdraw.getWithStatus()){
                case 0:
                    ex.setWithStatus("审核中");
                    break;
                case 1:
                    ex.setWithStatus("成功");
                    break;
                case 2:
                    ex.setWithStatus("失败");
                    break;
                case 3:
                    ex.setWithStatus("取消");
                    break;
            }
            userWithdrawExcels.add(ex);
        }
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=withdraw.xlsx");
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "提现列表").head(UserWithdrawExcel.class).build();
            excelWriter.write(userWithdrawExcels, writeSheet1);
            excelWriter.finish();
        } catch (Exception var10) {
            var10.printStackTrace();
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
    @Transactional
    public ServerResponse outMoney(String amt, String with_Pwd, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(amt)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }
        User user = this.iUserService.getCurrentRefreshUser(request);
        String w = user.getWithPwd();
        if (w == null) {
            w = "";
        }
        if (with_Pwd == null) {
            with_Pwd = "";
        }
        if (w.equals(with_Pwd)) {
            if (user.getIsLogin().intValue() == 1) {
                return ServerResponse.createByErrorMsg("用户被锁定");
            }


            List<UserPosition> userPositions = this.iUserPositionService.findPositionByUserIdAndSellIdIsNull(user.getId());

            if (userPositions.size() > 0) {

//                return ServerResponse.createByErrorMsg("有持仓单不能出金");

            }


            if (StringUtils.isBlank(user.getRealName()) || StringUtils.isBlank(user.getIdCard())) {

                return ServerResponse.createByErrorMsg("未实名认证");

            }

            UserBank userBank = this.iUserBankService.findUserBankByUserId(user.getId());

            if (userBank == null) {

                return ServerResponse.createByErrorMsg("未添加银行卡");

            }


            if (user.getAccountType().intValue() == 1) {

                return ServerResponse.createByErrorMsg("模拟用户不能出金");

            }


            SiteSetting siteSetting = this.iSiteSettingService.getSiteSetting();

            if ((new BigDecimal(amt)).compareTo(new BigDecimal(siteSetting.getWithMinAmt().intValue())) == -1) {

                return ServerResponse.createByErrorMsg("出金金额不得低于" + siteSetting.getWithMinAmt() + "元");

            }


            int with_time_begin = siteSetting.getWithTimeBegin().intValue();

            int with_time_end = siteSetting.getWithTimeEnd().intValue();

            if (!WithDrawUtils.checkIsWithTime(with_time_begin, with_time_end)) {

                return ServerResponse.createByErrorMsg("出金失败，出金时间在" + with_time_begin + "点 - " + with_time_end + "点 之间");

            }


//            BigDecimal index_user_amt = user.getUserIndexAmt();
////
////            if (index_user_amt.compareTo(new BigDecimal("0")) == -1) {
////
////                return ServerResponse.createByErrorMsg("指数资金不能小于0");
////
////            }
////
////
////            BigDecimal futures_user_amt = user.getUserFutAmt();
////
////            if (futures_user_amt.compareTo(new BigDecimal("0")) == -1) {
////
////                return ServerResponse.createByErrorMsg("期货资金不能小于0");
////
////            }


            BigDecimal enable_amt = user.getUserCapital();

            int compareAmt = enable_amt.compareTo(new BigDecimal(amt));

            if (compareAmt == -1) {

                return ServerResponse.createByErrorMsg("提现失败，用户可用资金不足");

            }


            BigDecimal user_all_amt = user.getUserCapital();

            BigDecimal reckon_all_amt = user_all_amt.subtract(new BigDecimal(amt));

            user.setUserCapital(reckon_all_amt);

            log.info("用户提现{}，金额 = {},总资金 = {},可用资金 = {}", new Object[]{user.getId(), amt, user_all_amt, enable_amt});


            log.info("提现后，总金额={},", reckon_all_amt);

            int updateUserCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateUserCount > 0) {

                log.info("修改用户资金成功");

            } else {

                log.error("修改用户资金失败");

                throw new Exception("用户提现，修改用户资金失败");

            }


            UserWithdraw userWithdraw = new UserWithdraw();

            userWithdraw.setUserId(user.getId());

            userWithdraw.setNickName(user.getRealName());

            userWithdraw.setAgentId(user.getAgentId());

            userWithdraw.setWithAmt(new BigDecimal(amt));

            userWithdraw.setApplyTime(new Date());

            userWithdraw.setWithName(user.getRealName());

            userWithdraw.setBankNo(userBank.getBankNo());

            userWithdraw.setBankName(userBank.getBankName());

            userWithdraw.setBankAddress(userBank.getBankAddress());

            userWithdraw.setWithStatus(Integer.valueOf(0));


            BigDecimal withfee = siteSetting.getWithFeePercent().multiply(new BigDecimal(amt)).add(new BigDecimal(siteSetting.getWithFeeSingle().intValue()));

            userWithdraw.setWithFee(withfee);

            if(userWithdraw.getAdminId() == null){
                userWithdraw.setAdminId(0);
            }

            int insertCount = this.userWithdrawMapper.insert(userWithdraw);

            if (insertCount > 0) {

                return ServerResponse.createBySuccessMsg("提现成功");

            }

            log.error("保存提现记录失败");

            throw new Exception("用户提现，保存提现记录失败");
        } else {
            return ServerResponse.createByErrorMsg("提现密码不正确！！");
        }

    }


    public ServerResponse<PageInfo> findUserWithList(String withStatus, HttpServletRequest request, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);


        User user = this.iUserService.getCurrentUser(request);


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.findUserWithList(user.getId(), withStatus);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse userCancel(Integer withId) {

        if (withId == null) {

            return ServerResponse.createByErrorMsg("id不能为空");

        }


        UserWithdraw userWithdraw = this.userWithdrawMapper.selectByPrimaryKey(withId);

        if (userWithdraw == null) {

            return ServerResponse.createByErrorMsg("订单不存在");

        }


        if (0 != userWithdraw.getWithStatus().intValue()) {

            return ServerResponse.createByErrorMsg("当前订单不能取消");

        }


        userWithdraw.setWithStatus(Integer.valueOf(3));

        userWithdraw.setWithMsg("用户取消出金");

        int updateCount = this.userWithdrawMapper.updateByPrimaryKeySelective(userWithdraw);

        if (updateCount > 0) {

            log.info("修改用户提现订单 {} 状态成功", withId);


            User user = this.userMapper.selectByPrimaryKey(userWithdraw.getUserId());

            user.setUserCapital(user.getUserCapital().add(userWithdraw.getWithAmt()));

//            user.setUserAmt(user.getUserAmt().add(userWithdraw.getWithAmt()));
//
//            user.setEnableAmt(user.getEnableAmt().add(userWithdraw.getWithAmt()));

            int updateUserCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateUserCount > 0) {

                log.info("反还用户资金，总 {} 可用 {}", user.getUserAmt(), user.getEnableAmt());

                return ServerResponse.createBySuccessMsg("取消成功");

            }

            return ServerResponse.createByErrorMsg("取消失败");

        }


        log.info("修改用户提现订单 {} 状态失败", withId);

        return ServerResponse.createByErrorMsg("取消失败");

    }


    public ServerResponse listByAgent(Integer agentId, String realName, Integer state, HttpServletRequest request, int pageNum, int pageSize) {

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


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.listByAgent(searchId, realName, state);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse<PageInfo> listByAdmin(Integer agentId, Integer userId, String realName, Integer state, String beginTime, String endTime, HttpServletRequest request, int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);


        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.listByAdmin(agentId, userId, realName, state, beginTime, endTime);


        PageInfo pageInfo = new PageInfo(userWithdraws);


        return ServerResponse.createBySuccess(pageInfo);

    }


    public ServerResponse updateState(Integer withId, Integer state, String authMsg) throws Exception {

        UserWithdraw userWithdraw = this.userWithdrawMapper.selectByPrimaryKey(withId);

        if (userWithdraw == null) {

            return ServerResponse.createByErrorMsg("提现订单不存在");

        }


        if (userWithdraw.getWithStatus().intValue() != 0) {

            return ServerResponse.createByErrorMsg("提现订单已处理，不要重复操作");

        }


        if (state.intValue() == 2 &&

                StringUtils.isBlank(authMsg)) {

            return ServerResponse.createByErrorMsg("失败信息必填");

        }


        if (state.intValue() == 2) {


            User user = this.userMapper.selectByPrimaryKey(userWithdraw.getUserId());

            if (user == null) {

                return ServerResponse.createByErrorMsg("用户不存在");

            }

//            BigDecimal user_amt = user.getUserAmt().add(userWithdraw.getWithAmt());

            BigDecimal user_capital = user.getUserCapital().add(userWithdraw.getWithAmt());

            log.info("管理员确认提现订单失败，返还用户 {} 总资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getUserAmt(), user_capital});

            user.setUserCapital(user_capital);

//            user.setUserAmt(user_amt);

//            BigDecimal user_enable_amt = user.getEnableAmt().add(userWithdraw.getWithAmt());

            log.info("管理员确认提现订单失败，返还用户 {} 可用资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getEnableAmt(), user_capital});

//            user.setEnableAmt(user_enable_amt);


            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateCount > 0) {

                log.info("提现失败，返还用户资金成功！");

            } else {

                log.error("返还用户资金出错，抛出异常");

                throw new Exception("修改用户资金出错，抛出异常");

            }


            userWithdraw.setWithMsg(authMsg);

        }


        userWithdraw.setWithStatus(Integer.valueOf((state.intValue() == 1) ? 1 : 2));


        userWithdraw.setTransTime(new Date());

        Integer adminId = AdminThreadLocal.get().getId();

        userWithdraw.setAdminId(adminId);


        int updateCount = this.userWithdrawMapper.updateByPrimaryKeySelective(userWithdraw);

        if (updateCount > 0) {

            return ServerResponse.createBySuccessMsg("操作成功！");

        }

        return ServerResponse.createByErrorMsg("操作失败！");

    }

    @Transactional
    public ServerResponse batchUpdateState(List<Integer> withIds, Integer state, String authMsg) throws Exception{

        List<UserWithdraw> userWithdraws = this.userWithdrawMapper.getListByWithdrawIds(withIds);

        if (state.intValue() == 2 && StringUtils.isBlank(authMsg)) {

            return ServerResponse.createByErrorMsg("失败信息必填");

        }

        for (UserWithdraw userWithdraw: userWithdraws){
            if(userWithdraw.getWithStatus().intValue() !=0 ){
                return ServerResponse.createByErrorMsg("已有处理过的订单，请重新选择！");
            }
        }

        for (UserWithdraw userWithdraw: userWithdraws) {

            if (state.intValue() == 2) {

                User user = this.userMapper.selectByPrimaryKey(userWithdraw.getUserId());

                if (user == null) {

                    return ServerResponse.createByErrorMsg("用户"+userWithdraw.getUserId()+"不存在");

                }

                BigDecimal user_amt = user.getUserAmt().add(userWithdraw.getWithAmt());

                log.info("管理员确认提现订单失败，返还用户 {} 总资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getUserAmt(), user_amt});

                user.setUserAmt(user_amt);

                BigDecimal user_enable_amt = user.getEnableAmt().add(userWithdraw.getWithAmt());

                log.info("管理员确认提现订单失败，返还用户 {} 可用资金，原金额 = {} , 返还后 = {}", new Object[]{user.getId(), user.getEnableAmt(), user_enable_amt});

                user.setEnableAmt(user_enable_amt);


                int updateCount = this.userMapper.updateByPrimaryKeySelective(user);

                if (updateCount > 0) {

                    log.info("提现失败，返还用户资金成功！");

                } else {

                    log.error("返还用户资金出错，抛出异常");

                    throw new Exception("修改用户资金出错，抛出异常");

                }

                userWithdraw.setWithMsg(authMsg);

            }

            userWithdraw.setWithStatus(Integer.valueOf((state.intValue() == 1) ? 1 : 2));


            userWithdraw.setTransTime(new Date());

            Integer adminId = AdminThreadLocal.get().getId();

            userWithdraw.setAdminId(adminId);

            int updateCount = this.userWithdrawMapper.updateByPrimaryKeySelective(userWithdraw);

            if (updateCount == 0) {

                return ServerResponse.createByErrorMsg("操作失败！");

            }

        }

        return ServerResponse.createBySuccessMsg("操作成功！");
    }

    public int deleteByUserId(Integer userId) {
        return this.userWithdrawMapper.deleteByUserId(userId);
    }


    public BigDecimal CountSpWithSumAmtByState(Integer withState) {
        return this.userWithdrawMapper.CountSpWithSumAmtByState(withState);
    }

    public BigDecimal CountSpWithSumAmTodaytByState(Integer withState) {
        return this.userWithdrawMapper.CountSpWithSumAmTodaytByState(withState);
    }

}

