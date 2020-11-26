package com.xc.pojo;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.Date;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
public class UserExcel   extends BaseRowModel{

    @ExcelProperty(value = {"所属代理ID"}, index = 0)
    private Integer agentId;
    @ExcelProperty(value = {"所属代理名称"}, index = 1)
    private String agentName;
    @ColumnWidth(15)
    @ExcelProperty(value = {"手机号"}, index = 3)
    private String phone;
    @ExcelProperty(value = {"真实姓名"}, index = 2)
    private String realName;


    @ExcelProperty(value = {"本金池"}, index = 5)
    private BigDecimal sumChargeAmt;
    @ExcelProperty(value = {"交易状态"}, index = 7)
    private String isLock;
    @ExcelProperty(value = {"登录状态"}, index = 8)
    private String isLogin;
    @ColumnWidth(25)
    @ExcelProperty(value = {"注册时间"}, index = 9)
    private Date regTime;
    @ExcelProperty(value = {"认证信息"}, index = 6)
    private String isActive;
    /*总操盘金额*/
    @ExcelProperty(value = {"总资产"}, index = 4)
    private BigDecimal tradingAmount;
    @ColumnWidth(25)
    @ExcelProperty(value = {"银行名称"}, index = 10)
    private String bankName;
    @ColumnWidth(25)
    @ExcelProperty(value = {"银行卡账号"}, index = 12)
    private String bankNo;
    @ColumnWidth(25)
    @ExcelProperty(value = {"银行支行"}, index = 11)
    private String bankAddress;
}
