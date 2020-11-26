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
public class StockTask {
    @Autowired
    IStockService stockService;
    private static final Logger log = LoggerFactory.getLogger(StockTask.class);

    public StockTask() {
    }

    public void time(boolean am, boolean pm) {
        am = false;
        pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("am = {}  pm = {}", am, pm);
    }

    /*股票走势图定时任务*/
    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void z1() {
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
            log.info("=====z1={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z1();
            log.info("=====z1={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void z11() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info(" am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====z11={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z11();
            log.info("====z11={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * ?"
    )
    public void z12() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z12 am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====z12={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z12();
            log.info("====z12={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z2() {
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
            this.stockService.z2();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z21() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z21-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z21();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z22() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z22-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z22();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z3() {
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
            this.stockService.z3();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z31() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z31-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z31();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z32() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z32-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z32();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z4() {
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
            this.stockService.z4();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z41() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z41-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z41();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }

    @Scheduled(
            cron = "0 0/1 9-15 * * MON-FRI"
    )
    public void z42() {
        boolean am = false;
        boolean pm = false;

        try {
            am = BuyAndSellUtils.isTransTime("9:29", "11:31");
            pm = BuyAndSellUtils.isTransTime("12:59", "15:00");
        } catch (Exception var4) {
            log.error("= {}", var4);
        }

        log.info("z42-am = {}  pm = {}", am, pm);
        if (am || pm) {
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
            this.stockService.z42();
            log.info("====={} =====", DateTimeUtil.dateToStr(new Date()));
        }

    }
}
