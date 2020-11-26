
package com.xc.utils.task.stock;

import com.xc.service.IStockMarketsDayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SaveDayMarketsTask {
    private static final Logger log = LoggerFactory.getLogger(SaveDayMarketsTask.class);
    @Autowired
    IStockMarketsDayService iStockMarketsDayService;

    public SaveDayMarketsTask() {
    }

    @Scheduled(
            cron = "0 0 16 ? * MON-FRI"
    )
    public void banlanceUserPositionTaskV1() {
        this.dotask();
    }

    public void dotask() {
        this.iStockMarketsDayService.saveStockMarketDay();
    }
}
