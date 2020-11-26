package com.xc.controller;


import com.xc.common.ServerResponse;

import com.xc.pojo.StockFutures;
import com.xc.service.IStockFuturesService;

import com.xc.vo.stockfutures.FuturesVO;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping({"/api/futures/"})
public class StockFuturesApiController {

    private static final Logger log = LoggerFactory.getLogger(StockFuturesApiController.class);

    @Autowired
    IStockFuturesService iStockFuturesService;

    //查询所有首页显示的期货信息
    @RequestMapping({"queryHome.do"})
    @ResponseBody
    public ServerResponse queryHome() {
        return this.iStockFuturesService.queryHome();
    }


    //查询所有列表显示的期货信息
    @RequestMapping({"queryList.do"})
    @ResponseBody
    public ServerResponse queryList(HttpServletRequest request) {
        return this.iStockFuturesService.queryList(request);
    }

    @RequestMapping({"queryTrans.do"})
    @ResponseBody
    public ServerResponse queryTrans(@RequestParam("futuresId") Integer futuresId) {
        return this.iStockFuturesService.queryTrans(futuresId);
    }

    //查询汇率
    @RequestMapping({"queryExchange.do"})
    @ResponseBody
    public ServerResponse queryExchange(@RequestParam("coinCode") String coinCode) {
        return this.iStockFuturesService.getExchangeRate(coinCode);
    }

    //查询期货详情信息 （开盘价/收盘价/最高/最低等等。。。）
    @RequestMapping({"querySingleMarket.do"})
    @ResponseBody
    public ServerResponse querySingleMarket(@RequestParam("futuresGid") String futuresGid) {
        FuturesVO futuresVO = this.iStockFuturesService.querySingleMarket(futuresGid);
        return ServerResponse.createBySuccess(futuresVO);
    }

    //查询期货详情
    @RequestMapping({"queryFuturesByCode.do"})
    @ResponseBody
    public ServerResponse queryFuturesByCode(@RequestParam("futuresCode") String futuresCode) {
        StockFutures stockFutures = this.iStockFuturesService.selectFuturesByCode(futuresCode);
        return ServerResponse.createBySuccess(stockFutures);
    }
}
