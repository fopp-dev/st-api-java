package com.xc.service.impl;


import com.xc.dao.StockMarketsDayMapper;

import com.xc.pojo.Stock;

import com.xc.pojo.StockMarketsDay;

import com.xc.service.IStockMarketsDayService;

import com.xc.service.IStockService;


import com.xc.utils.DateTimeUtil;

import com.xc.utils.stock.sina.SinaStockApi;

import com.xc.vo.stock.StockListVO;

import java.math.BigDecimal;

import java.util.Date;

import java.util.List;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service("iStockMarketsDayService")
public class StockMarketsDayServiceImpl implements IStockMarketsDayService {

    private static final Logger log = LoggerFactory.getLogger(StockMarketsDayServiceImpl.class);

    @Autowired
    IStockService iStockService;

    @Autowired
    StockMarketsDayMapper stockMarketsDayMapper;


    public void saveStockMarketDay() {
        log.info("【保存股票日内行情 定时任务】 开始保存 ... ");

        List<Stock> stockList = this.iStockService.findStockList();

    }


    public BigDecimal selectRateByDaysAndStockCode(Integer stockId, int days) {
        return this.stockMarketsDayMapper.selectRateByDaysAndStockCode(stockId, Integer.valueOf(days));
    }

}

