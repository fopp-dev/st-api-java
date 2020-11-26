package com.xc.utils.task.stock;

import com.xc.service.IStockService;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.stock.BuyAndSellUtils;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockShTask {
    @Autowired
    IStockService stockService;
    private static final Logger log = LoggerFactory.getLogger(StockTask.class);

    public StockShTask() {
    }

    @Scheduled(
            cron = "0 0/1 5-15 * * ?"
    )
    public void h1() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h1();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 5-15 * * ?"
    )
    public void h11() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h11-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h11();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 5-15 * * ?"
    )
    public void h12() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h12-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h12();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h2() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h2();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h21() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h21-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h21();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h22() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h22-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h22();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h3() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h3();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h31() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h31-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h31();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void h32() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("h32-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.h32();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }
}
