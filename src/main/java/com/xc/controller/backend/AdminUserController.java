package com.xc.controller.backend;


import com.xc.common.ServerResponse;
import com.xc.pojo.User;
import com.xc.pojo.UserBank;
import com.xc.service.IUserBankService;
import com.xc.service.IUserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping({"/admin/user/"})
public class AdminUserController {
    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    IUserService iUserService;

    @Autowired
    IUserBankService iUserBankService;

    //分页查询所有用户列表信息 及模糊查询用户信息
    @RequestMapping({"list.do"})
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "realName", required = false) String realName, @RequestParam(value = "phone", required = false) String phone, @RequestParam(value = "agentId", required = false) Integer agentId, @RequestParam(value = "accountType", required = false) Integer accountType, @RequestParam(value = "isActive", required = false) Integer isActive, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpServletRequest request) {
        return this.iUserService.listByAdmin(realName, phone, agentId, accountType,isActive, pageNum, pageSize, request);
    }
    //导出
    @RequestMapping({"exportList.do"})
    @ResponseBody
    public void exportList(@RequestParam(value = "realName", required = false) String realName, @RequestParam(value = "phone", required = false) String phone, @RequestParam(value = "agentId", required = false) Integer agentId, @RequestParam(value = "accountType", required = false) Integer accountType, @RequestParam(value = "isActive", required = false) Integer isActive, HttpServletResponse response) {
        this.iUserService.listExportByAdmin(realName, phone, agentId, accountType,isActive,response);
    }
    //查询用户信息是否存在
    @RequestMapping({"detail.do"})
    @ResponseBody
    public ServerResponse detail(Integer userId) {
        return this.iUserService.findByUserId(userId);
    }

    @RequestMapping({"updateLock.do"})
    @ResponseBody
    public ServerResponse updateLock(Integer userId) {
        return this.iUserService.updateLock(userId);
    }

    //修改用户列表 用户资金入款/扣款
    @RequestMapping({"updateAmt.do"})
    @ResponseBody
    public ServerResponse updateAmt(Integer userId, Integer amt, Integer direction, Integer type) {
        return this.iUserService.updateAmt(userId, amt, direction, type);
    }

    //修改用户列表 用户信息
    @RequestMapping({"update.do"})
    @ResponseBody
    public ServerResponse update(User user) {
        return this.iUserService.update(user);
    }

    //修改用户列表 银行卡信息
    @RequestMapping({"updateBank.do"})
    @ResponseBody
    public ServerResponse updateBank(UserBank userBank) {
        return this.iUserBankService.updateBankByAdmin(userBank);
    }

    //添加用户列表 用户信息
    @RequestMapping({"addSimulatedAccount.do"})
    @ResponseBody
    public ServerResponse addSimulatedAccount(HttpServletRequest request, @RequestParam(value = "agentId", required = false) Integer agentId, @RequestParam("phone") String phone, @RequestParam("amt") String amt, @RequestParam("accountType") Integer accountType, @RequestParam("pwd") String pwd) {
        return this.iUserService.addSimulatedAccount(agentId, phone, pwd, amt, accountType, request);
    }

    //用户认证
    @RequestMapping({"authByAdmin.do"})
    @ResponseBody
    public ServerResponse authByAdmin(Integer userId, Integer state, String authMsg) {
        return this.iUserService.authByAdmin(userId, state, authMsg);
    }

    //查看指定 用户列表的用户信息
    @RequestMapping({"getBank.do"})
    @ResponseBody
    public ServerResponse getBank(Integer userId) {
        return this.iUserBankService.getBank(userId);
    }

    //删除用户列表 用户信息
    @RequestMapping({"delete.do"})
    @ResponseBody
    public ServerResponse delete(Integer userId, HttpServletRequest request) {
        return this.iUserService.delete(userId, request);
    }

    //用户摘要列表
    @RequestMapping({"abstractList.do"})
    @ResponseBody
    public ServerResponse abstractList(@RequestParam(value = "realName", required = false) String realName, @RequestParam(value = "userId", required = false) String phone, @RequestParam(value = "agentId", required = false) Integer agentId, @RequestParam(value = "accountType", required = false) Integer accountType, @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime, @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpServletRequest request) {
        return this.iUserService.abstractListByAdmin(realName, phone, agentId, accountType,startTime,endTime, pageNum, pageSize, request);
    }
}
