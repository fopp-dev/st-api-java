package com.xc.controller;

import com.xc.common.ServerResponse;
import com.xc.service.IStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping({"/api/stock/"})
public class StockApiController {
    private static final Logger log = LoggerFactory.getLogger(StockApiController.class);

    @Autowired
    IStockService iStockService;

    //查询 股票指数、大盘指数信息
    @RequestMapping({"getMarket.do"})
    @ResponseBody
    public ServerResponse getMarket() {
        return this.iStockService.getMarket();
    }

    //查询官网PC端交易 所有股票信息及指定股票信息
    @RequestMapping({"getStock.do"})
    @ResponseBody
    public ServerResponse getStock(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "stockPlate", required = false) String stockPlate, @RequestParam(value = "stockType", required = false) String stockType, @RequestParam(value = "keyWords", required = false) String keyWords, HttpServletRequest request) {
        return this.iStockService.getStock(pageNum, pageSize, keyWords, stockPlate, stockType, request);
    }

    //通过股票代码查询股票信息
    @RequestMapping({"getSingleStock.do"})
    @ResponseBody
    public ServerResponse getSingleStock(@RequestParam("code") String code) {
        return this.iStockService.getSingleStock(code);
    }

    @RequestMapping({"getMinK.do"})
    @ResponseBody
    public ServerResponse getMinK(@RequestParam("code") String code, @RequestParam("time") Integer time, @RequestParam("ma") Integer ma, @RequestParam("size") Integer size) {
        return this.iStockService.getMinK(code, time, ma, size);
    }

    /*查询股票日线*/
    @RequestMapping({"getDayK.do"})
    @ResponseBody
    public ServerResponse getDayK(@RequestParam("code") String code) {
        return this.iStockService.getDayK_Echarts(code);
    }

    //查询股票历史数据数据
    @RequestMapping({"getMinK_Echarts.do"})
    @ResponseBody
    public ServerResponse getMinK_Echarts(@RequestParam("code") String code, @RequestParam("time") Integer time, @RequestParam("ma") Integer ma, @RequestParam("size") Integer size) {
        return this.iStockService.getMinK_Echarts(code, time, ma, size);
    }
    //查询股票历史数据数据
    @RequestMapping({"getGGMinK_Echarts.do"})
    @ResponseBody
    public ServerResponse getGGMinK_Echarts(@RequestParam("code") String code,@RequestParam("time") Integer time,@RequestParam("size") Integer size) {
        return this.iStockService.getGGMinK_Echarts(code, time, size);
    }
    //查询股票分时数据
    @RequestMapping({"findGGStock.do"})
    @ResponseBody
    public ServerResponse findStock(@RequestParam("code") String code) {
        return this.iStockService.findStock(code);
    }
    //通过股票代码查询股票信息
    @RequestMapping({"getGGSingleStock.do"})
    @ResponseBody
    public ServerResponse getGGSingleStock(@RequestParam("code") String code) {
        return this.iStockService.getGGSingleStock(code);
    }
    /*期货分时-k线*/
    @RequestMapping({"getFuturesMinK_Echarts.do"})
    @ResponseBody
    public ServerResponse getFuturesMinK_Echarts(@RequestParam("code") String code, @RequestParam("time") Integer time, @RequestParam("size") Integer size) {
        return this.iStockService.getFuturesMinK_Echarts(code, time, size);
    }

    /*指数分时-k线*/
    @RequestMapping({"getIndexMinK_Echarts.do"})
    @ResponseBody
    public ServerResponse getIndexMinK_Echarts(@RequestParam("code") String code, @RequestParam("time") Integer time, @RequestParam("size") Integer size) {
        return this.iStockService.getIndexMinK_Echarts(code, time, size);
    }

    /*查询期货日线*/
    @RequestMapping({"getFuturesDayK.do"})
    @ResponseBody
    public ServerResponse getFuturesDayK(@RequestParam("code") String code) {
        return this.iStockService.getFuturesDayK(code);
    }

    /*指数日线*/
    @RequestMapping({"getIndexDayK.do"})
    @ResponseBody
    public ServerResponse getIndexDayK(@RequestParam("code") String code) {
        return this.iStockService.getIndexDayK(code);
    }
}