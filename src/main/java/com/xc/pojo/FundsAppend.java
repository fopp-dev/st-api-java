package com.xc.pojo;

import java.io.Serializable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  配资追加申请
 * @author lr 2020-08-01
 */
@Data
public class FundsAppend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 追加类型：1、扩大配资，2、追加保证金，3、续期，4、终止操盘，5、提前盈利
     */
    private Integer appendType;

    /**
     * 申请子账号id
     */
    private Integer applyId;

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
     * 操盘期限
     */
    private Integer tradersCycle;

    /**
     * 杠杆
     */
    private Integer lever;

    /**
     * 管理费
     */
    private BigDecimal manageFee;

    /**
     * 总操盘金额
     */
    private BigDecimal totalTradingAmount;

    /**
     * 追加期限
     */
    private Integer appendCycle;

    /**
     * 追加服务费
     */
    private BigDecimal appendServiceFee;

    /**
     * 追加保证金
     */
    private BigDecimal appendMargin;

    /**
     * 状态：0、申请中，1、已通过，2、未通过
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
     * 结束时间
     */
    private Date endTime;

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

    /**
     * 应付总金额
     */
    private BigDecimal payAmount;

    public FundsAppend() {
    }

}
