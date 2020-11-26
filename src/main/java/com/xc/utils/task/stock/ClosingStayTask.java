package com.xc.utils.task.stock;

import com.xc.service.IUserGgPositionService;
import com.xc.service.IUserPositionService;
import com.xc.utils.DateTimeUtil;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClosingStayTask {
    private static final Logger log = LoggerFactory.getLogger(ClosingStayTask.class);
    @Autowired
    IUserPositionService iUserPositionService;
    @Autowired
    IUserGgPositionService iUserGgPositionService;

    public ClosingStayTask() {
    }

    @Scheduled(
            cron = "0 0 7 ? * 2,3,4,5,6"
    )
    public void closingStayV1() {
        log.info("=======================收盘收取留仓费任务开始 ===========================");
        log.info("收盘收取留仓费任务 开始时间 = {}", DateTimeUtil.dateToStr(new Date()));
        log.info("");
        this.dotask();
        log.info("");
        log.info("收盘收取留仓费任务 结束时间 = {}", DateTimeUtil.dateToStr(new Date()));
        log.info("=======================收盘收取留仓费任务结束 ===========================");
    }

    public void dotask() {
        this.iUserPositionService.doClosingStayTask();
        this.iUserGgPositionService.doClosingStayTask();
    }

    @Scheduled(
            cron = "0 0 15 ? * MON-FRI"
    )
    public void expireStayUnwind() {
        log.info("=======================留仓到期强制平仓任务开始 ===========================");
        log.info("留仓到期强制平仓 开始时间 = {}", DateTimeUtil.dateToStr(new Date()));
        this.iUserPositionService.expireStayUnwindTask();
        this.iUserGgPositionService.expireStayUnwindTask();
        log.info("留仓到期强制平仓 结束时间 = {}", DateTimeUtil.dateToStr(new Date()));
        log.info("=======================留仓到期强制平仓任务结束 ===========================");
    }
}
