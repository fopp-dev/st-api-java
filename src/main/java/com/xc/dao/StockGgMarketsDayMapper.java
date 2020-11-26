package com.xc.dao;

import com.xc.pojo.StockGgMarketsDay;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface StockGgMarketsDayMapper {
    int deleteByPrimaryKey(Integer paramInteger);

    int insert(StockGgMarketsDay paramStockMarketsDay);

    int insertSelective(StockGgMarketsDay paramStockMarketsDay);

    StockGgMarketsDay selectByPrimaryKey(Integer paramInteger);

    int updateByPrimaryKeySelective(StockGgMarketsDay paramStockMarketsDay);

    int updateByPrimaryKey(StockGgMarketsDay paramStockMarketsDay);

    BigDecimal selectRateByDaysAndStockCode(@Param("stockId") Integer paramInteger1, @Param("days") Integer paramInteger2);
}
