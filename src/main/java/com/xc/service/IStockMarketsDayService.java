package com.xc.service;


import java.math.BigDecimal;

public interface IStockMarketsDayService {
  void saveStockMarketDay();
  
  BigDecimal selectRateByDaysAndStockCode(Integer paramInteger, int paramInt);
}

