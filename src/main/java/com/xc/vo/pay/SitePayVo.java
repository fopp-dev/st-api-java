package com.xc.vo.pay;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SitePayVo {
    private Integer id;
    private Integer cType;
    private String formUrl;
    private String formCode;
    private String channelType;
    private String channelName;
    private String channelDesc;
    private String channelAccount;
    private String channelImg;
    private Integer channelMinLimit;
    private Integer channelMaxLimit;
    private Integer isShow;
    private Integer isLock;
    /*累计充值金额*/
    private BigDecimal totalPrice;

    private BigDecimal totalAllowPay;

    private BigDecimal serverCharge;

    private BigDecimal surTotalAllowPay;
}
