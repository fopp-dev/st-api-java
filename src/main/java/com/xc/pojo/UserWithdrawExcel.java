package com.xc.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
public class UserWithdrawExcel  extends BaseRowModel {
    @ExcelProperty(value = {"id"}, index = 0)
    private Integer id;
    @ExcelProperty(value = {"用户名"}, index = 2)
    private String nickName;
    @ExcelProperty(value = {"代理Id"}, index = 1)
    private Integer agentId;

    @ExcelProperty(value = {"应转金额"}, index = 3)
    private BigDecimal needAmt;
    @ExcelProperty(value = {"出金金额"}, index = 4)
    private BigDecimal withAmt;

    @ColumnWidth(25)
    @ExcelProperty(value = {"申请时间"}, index = 11)
    private Date applyTime;
    @ColumnWidth(25)
    @ExcelProperty(value = {"出金时间"}, index = 12)
    private Date transTime;
    @ColumnWidth(25)
    @ExcelProperty(value = {"银行号码"}, index = 9)
    private String bankNo;

    @ExcelProperty(value = {"提现银行"}, index = 7)
    private String bankName;
    @ColumnWidth(25)
    @ExcelProperty(value = {"提现支行"}, index = 8)
    private String bankAddress;
    @ExcelProperty(value = {"状态"}, index = 6)
    private String withStatus;
    @ExcelProperty(value = {"手续费"}, index = 5)
    private BigDecimal withFee;
    @ColumnWidth(25)
    @ExcelProperty(value = {"原因"}, index = 10)
    private String withMsg;




}