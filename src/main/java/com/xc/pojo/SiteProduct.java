package com.xc.pojo;

import com.xc.pojo.SiteProduct;


public class SiteProduct {
    private Integer id;
    private Boolean stockDisplay;
    private Boolean kcStockDisplay;
    private Boolean indexDisplay;
    private Boolean futuresDisplay;
    /*实名认证开关：1、开启，0、关闭*/
    private Boolean realNameDisplay;
    /**
     * 分仓配资总开关
     */
    private boolean fundsDisplay;

    /**
     * 分仓配资续期审核开关
     */
    private boolean delayDisplay;

    /**
     * 分仓扩大配资审核开关
     */
    private boolean expandDisplay;

    /**
     * 分仓追加保证金审核开关
     */
    private boolean marginDisplay;

    /**
     * 分仓终止操盘审核开关
     */
    private boolean endDisplay;

    public SiteProduct(Integer id, Boolean stockDisplay, Boolean kcStockDisplay, Boolean indexDisplay, Boolean futuresDisplay, Boolean realNameDisplay, Boolean fundsDisplay, Boolean delayDisplay, Boolean expandDisplay, Boolean marginDisplay, Boolean endDisplay) {
        this.id = id;
        this.stockDisplay = stockDisplay;
        this.kcStockDisplay = kcStockDisplay;
        this.indexDisplay = indexDisplay;
        this.futuresDisplay = futuresDisplay;
        this.realNameDisplay = realNameDisplay;
        this.fundsDisplay = fundsDisplay;
        this.delayDisplay = delayDisplay;
        this.expandDisplay = expandDisplay;
        this.marginDisplay = marginDisplay;
        this.endDisplay = endDisplay;
    }



    public SiteProduct() {}


    public Integer getId() { return this.id; }



    public void setId(Integer id) { this.id = id; }



    public Boolean getStockDisplay() { return this.stockDisplay; }



    public void setStockDisplay(Boolean stockDisplay) { this.stockDisplay = stockDisplay; }



    public Boolean getKcStockDisplay() { return this.kcStockDisplay; }



    public void setKcStockDisplay(Boolean kcStockDisplay) { this.kcStockDisplay = kcStockDisplay; }



    public Boolean getIndexDisplay() { return this.indexDisplay; }



    public void setIndexDisplay(Boolean indexDisplay) { this.indexDisplay = indexDisplay; }



    public Boolean getFuturesDisplay() { return this.futuresDisplay; }



    public void setFuturesDisplay(Boolean futuresDisplay) { this.futuresDisplay = futuresDisplay; }

    public Boolean getRealNameDisplay() {
        return realNameDisplay;
    }

    public void setRealNameDisplay(Boolean realNameDisplay) {
        this.realNameDisplay = realNameDisplay;
    }

    public boolean isFundsDisplay() {
        return fundsDisplay;
    }

    public void setFundsDisplay(boolean fundsDisplay) {
        this.fundsDisplay = fundsDisplay;
    }

    public boolean isDelayDisplay() {
        return delayDisplay;
    }

    public void setDelayDisplay(boolean delayDisplay) {
        this.delayDisplay = delayDisplay;
    }

    public boolean isExpandDisplay() {
        return expandDisplay;
    }

    public void setExpandDisplay(boolean expandDisplay) {
        this.expandDisplay = expandDisplay;
    }

    public boolean isMarginDisplay() {
        return marginDisplay;
    }

    public void setMarginDisplay(boolean marginDisplay) {
        this.marginDisplay = marginDisplay;
    }

    public boolean isEndDisplay() {
        return endDisplay;
    }

    public void setEndDisplay(boolean endDisplay) {
        this.endDisplay = endDisplay;
    }
}
