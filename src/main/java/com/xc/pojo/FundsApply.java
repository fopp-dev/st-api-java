package com.xc.pojo;

import java.io.Serializable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  配资申请
 * @author lr 2020-07-25
 */
@Data
public class FundsApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 申请人id
     */
    private Integer userId;

    /**
     * 申请人姓名
     */
    private String userName;

    /**
     * 申请人手机
     */
    private String userPhone;

    /**
     * 配资类型：1按天、2按周、3按月
     */
    private Integer fundsType;

    /**
     * 保证金
     */
    private BigDecimal margin;

    /**
     * 配资金额
     */
    private BigDecimal fundsAmount;

    /**
     * 杠杆
     */
    private Integer lever;

    /**
     * 总操盘金额
     */
    private BigDecimal totalTradingAmount;

    /**
     * 可用操盘金额
     */
    private BigDecimal enabledTradingAmount;

    /**
     * 支付金额/准备金额
     */
    private BigDecimal payAmount;

    /**
     * 操盘期限
     */
    private Integer tradersCycle;

    /**
     * 子账户编号，默认从80000000开始
     */
    private Integer subaccountNumber;

    /**
     * 管理费
     */
    private BigDecimal manageFee;

    /**
     * 状态：0、申请中，1、已通过，2、未通过，3、已过期，4、结束配资
     */
    private Integer status;

    /**
     * 添加时间
     */
    private Date addTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 开始时间
     */
    private Date beginTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 预警线
     */
    private BigDecimal lineWarning;

    /**
     * 平仓线
     */
    private BigDecimal lineUnwind;

    /**
     * 预警比例
     */
    private BigDecimal ratioWarning;

    /**
     * 平仓比例
     */
    private BigDecimal ratioUnwind;

    public FundsApply() {
    }

}
