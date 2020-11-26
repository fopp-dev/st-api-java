package com.xc.pojo;
import java.io.Serializable;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  funds_setting
 * @author lr 2020-07-23
 */
@Data
public class FundsSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 保证金最小值
     */
    private Integer marginMin;

    /**
     * 保证金最大值
     */
    private Integer marginMax;

    /**
     * 免息杠杆
     */
    private Integer interestFreeLever;

    /**
     * 免息天数
     */
    private Integer interestFreeDays;

    /**
     * 免息盈利
     */
    private BigDecimal interestFreeProfit;

    /**
     * 免息预警线
     */
    private BigDecimal interestFreeWarning;

    /**
     * 免息平仓线
     */
    private BigDecimal interestFreeUnwind;

    /**
     * 按天预警线
     */
    private BigDecimal daysWarning;

    /**
     * 按天平仓线
     */
    private BigDecimal daysUnwind;

    /**
     * 按周预警线
     */
    private BigDecimal weeksWarning;

    /**
     * 按周平仓线
     */
    private BigDecimal weeksUnwind;

    /**
     * 按月预警线
     */
    private BigDecimal monthWarning;

    /**
     * 按月平仓线
     */
    private BigDecimal monthUnwind;

    /**
     * 按天使用期限，多个用中划线分割，如2|3
     */
    private String daysUsePeriod;

    /**
     * 按周使用期限，多个用中划线分割，如2|3
     */
    private String weeksUsePeriod;

    /**
     * 按月使用期限，多个用中划线分割，如2|3
     */
    private String monthUsePeriod;

    /**
     * 提前终止利息，扣除30%，填0.3
     */
    private BigDecimal earlyTerminationInterest;

    /**
     * 交易佣金费率，35%填0.35
     */
    private BigDecimal tradingCommissionRate;

    /**
     * 每笔最低费用
     */
    private Integer stampDutyRate;

    /**
     * 盈利分成比例
     */
    private BigDecimal profitSharingRatio;

    public FundsSetting() {
    }

}
