package com.xc.pojo;

import java.io.Serializable;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  配资交易账户
 * @author lr 2020-07-24
 */
@Data
public class FundsTradingAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 证券信息id
     */
    private Integer dealerInstitutionsId;

    /**
     * 证券信息机构名称
     */
    private String dealerInstitutionsName;

    /**
     * 证券账户名
     */
    private String accountName;

    /**
     * 子账户编号，默认从80000000开始
     */
    private Integer subaccountNumber;

    /**
     * 子账户模式：1、实盘账户，0、模拟账户
     */
    private Integer accountMode;

    /**
     * 自动平仓开关：1、开启，0、关闭
     */
    private boolean automaticUnwindSwitch;

    /**
     * 禁止平仓开关：1、开启，0、关闭
     */
    private boolean banUnwindSwitch;

    /**
     * 自动续期开关：1、开启，0、关闭
     */
    private boolean automaticRenewalSwitch;

    /**
     * 禁止入仓开关：1、开启，0、关闭
     */
    private boolean banLevite;

    /**
     * 按天预警线
     */
    private BigDecimal warningLine;

    /**
     * 按天平仓线
     */
    private BigDecimal unwindLine;

    /**
     * 单股持仓比例
     */
    private BigDecimal singleHoldingRatio;

    /**
     * 状态：1、已用，0、未用
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
     * 备注
     */
    private String remarks;

    public FundsTradingAccount() {
    }

}
