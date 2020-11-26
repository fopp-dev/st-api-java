package com.xc.controller;

import com.xc.common.ServerResponse;
import com.xc.service.IStockService;
import com.xc.service.RealTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/api/realTime/"})
public class RealTimeController {

    @Autowired
    RealTimeService realTimeService;

    @Autowired
    IStockService stockService;

    @RequestMapping({"findStock.do"})
    @ResponseBody
    public ServerResponse findStock(@RequestParam(value = "stockCode", required = false) String stockCode) {
        return this.realTimeService.findStock(stockCode);
    }

}
