package com.xc.pojo;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
public class UserRecharge extends BaseRowModel {
    @ExcelProperty(value = {"id"}, index = 0)
    private Integer id;
    @ExcelProperty(value = {"用户ID"}, index = 3)
    private Integer userId;
    @ExcelProperty(value = {"用户名"}, index = 2)
    private String nickName;
    @ExcelIgnore
    private Integer agentId;
    @ColumnWidth(25)
    @ExcelProperty(value = {"订单号"}, index = 1)
    private String orderSn;
    @ExcelIgnore
    private String paySn;
    @ExcelProperty(value = {"充值渠道"}, index = 4)
    private String payChannel;
    @ExcelProperty(value = {"充值金额"}, index = 5)
    private BigDecimal payAmt;
    @ColumnWidth(25)
    @ExcelProperty(value = {"订单状态 1为成功 0为失败"}, index = 6)
    private Integer orderStatus;
    @ExcelProperty(value = {"备注"}, index = 9)
    private String orderDesc;
    @ColumnWidth(25)
    @ExcelProperty(value = {"申请时间"}, index = 7)
    private Date addTime;
    @ColumnWidth(25)
    @ExcelProperty(value = {"支付时间"}, index = 8)
    private Date payTime;

    /*支付通道主键id*/
    @ExcelIgnore
    private Integer payId;

    //支付凭证
    private String vouImage;

    private Integer type;

    private Integer adminId;

    private SiteAdmin siteAdmin;

    private String orderAdminDesc;

    public SiteAdmin getSiteAdmin() {
        return siteAdmin;
    }

    public void setSiteAdmin(SiteAdmin siteAdmin) {
        this.siteAdmin = siteAdmin;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public UserRecharge(Integer id, Integer userId, String nickName, Integer agentId, String orderSn, String paySn, String payChannel, BigDecimal payAmt, Integer orderStatus, String orderDesc, Date addTime, Date payTime, Integer payId, String vouImage, Integer type, Integer adminId, String orderAdminDesc) {

        this.id = id;

        this.userId = userId;

        this.nickName = nickName;

        this.agentId = agentId;

        this.orderSn = orderSn;

        this.paySn = paySn;

        this.payChannel = payChannel;

        this.payAmt = payAmt;

        this.orderStatus = orderStatus;

        this.orderDesc = orderDesc;

        this.addTime = addTime;

        this.payTime = payTime;

        this.payId = payId;

        this.vouImage = vouImage;

        this.type = type;

        this.adminId = adminId;

        this.orderAdminDesc = orderAdminDesc;

//        this.siteAdmin = siteAdmin;
    }

    public UserRecharge() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public String getPaySn() {
        return paySn;
    }

    public void setPaySn(String paySn) {
        this.paySn = paySn;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }


    public String getVouImage() {
        return vouImage;
    }

    public void setVouImage(String vouImage) {
        this.vouImage = vouImage;
    }

    public String getOrderAdminDesc() {
        return orderAdminDesc;
    }

    public void setOrderAdminDesc(String orderAdminDesc) {
        this.orderAdminDesc = orderAdminDesc;
    }
}