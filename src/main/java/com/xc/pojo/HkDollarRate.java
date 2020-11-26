package com.xc.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HkDollarRate implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private BigDecimal inRate;

    private BigDecimal outRate;

    private BigDecimal inDiff;

    private BigDecimal outDiff;

    private BigDecimal realRate;
}
