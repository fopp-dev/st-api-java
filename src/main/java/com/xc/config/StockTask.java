package com.xc.config;

import com.xc.dao.RealTimeMapper;
import com.xc.pojo.RealTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

public class StockTask implements Runnable {
    @Resource
    RealTimeMapper realTimeMapper;

    private Object[] split;

    private String stockGid;

    /*均价*/
    private Double averagePrice;

    private StockPoll stockPoll;

    public void run() {
        RealTime realTime = new RealTime();
        realTime.setPrice(Double.parseDouble(this.split[1].toString()));
        realTime.setRates(Double.parseDouble(this.split[3].toString()));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date());
        realTime.setTime(time);
        realTime.setVolumes(Integer.parseInt(this.split[4].toString()));
        String s = this.split[5].toString();
        int index = s.indexOf("\"");
        String substring = s.substring(0, index);
        int amounts = Integer.parseInt(substring);
        realTime.setAmounts(amounts);
        realTime.setStockCode(this.stockGid);
        realTime.setAveragePrice(this.averagePrice);
        List<RealTime> realTimes = new ArrayList<>();
        realTimes.add(realTime);
        this.realTimeMapper.insertRealTime(realTimes);
    }

    public void splits(Object[] split) {
        this.split = split;
    }

    public void stockGid(String stockGid) {
        this.stockGid = stockGid;
    }

    public void averagePrice(Double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public void StockPoll(StockPoll stockPoll) {
        this.stockPoll = stockPoll;
    }

    public void RealTimeMapper(RealTimeMapper realTimeMapper) {
        this.realTimeMapper = realTimeMapper;
    }
}
