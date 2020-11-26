package com.xc.vo.user;

import com.xc.pojo.User;
import lombok.Data;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserBackInfoVo {
    private Integer id;
    private Integer agentId;
    private String agentName;
    private String phone;
    private String userPwd;
    private String withPwd;
    private String nickName;
    private String realName;
    private String idCard;


    private Integer accountType;

    private BigDecimal userCapital;

    private BigDecimal userHmt;

    private  BigDecimal enableHmt;

    private BigDecimal userAmt;

    private BigDecimal enableAmt;

    private BigDecimal sumChargeAmt;

    private BigDecimal sumBuyAmt;


    public void setId(Integer id) {
        this.id = id;
    }

    private String recomPhone;
    private Integer isLock;
    private Integer isLogin;
    private Date regTime;
    private String regIp;
    private String regAddress;
    private String img1Key;
    private String img2Key;
    private String img3Key;
    private Integer isActive;
    private String authMsg;
    private BigDecimal userIndexAmt;
    private BigDecimal enableIndexAmt;
    private BigDecimal userFutAmt;
    private BigDecimal enableFutAmt;
    private String withdrawalPwd;
    /*总操盘金额*/
    private BigDecimal tradingAmount;

    private String email;

    //港股资金池转汇率
    private BigDecimal userHmtMulRate;

    //用户总资产
    private BigDecimal totalCapital;

    // 用户A股本金
    private BigDecimal userStockACapital;

    // 用户港股本金
    private BigDecimal userStockHKCapital;

}
