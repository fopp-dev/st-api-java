package com.xc.controller;


import com.xc.common.ServerResponse;
import com.xc.service.IStockIndexService;
import com.xc.vo.stock.MarketVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping({"/api/index/"})
public class StockIndexApiController {
    private static final Logger log = LoggerFactory.getLogger(StockIndexApiController.class);

    @Autowired
    IStockIndexService iStockIndexService;

    //查询指数信息
    @RequestMapping({"queryHomeIndex.do"})
    @ResponseBody
    public ServerResponse queryHomeIndex() {
        return this.iStockIndexService.queryHomeIndex();
    }

    @RequestMapping({"queryListIndex.do"})
    @ResponseBody
    public ServerResponse queryListIndex(HttpServletRequest request) {
        return this.iStockIndexService.queryListIndex(request);
    }

    @RequestMapping({"queryTransIndex.do"})
    @ResponseBody
    public ServerResponse queryTransIndex(@RequestParam("indexId") Integer indexId) {
        return this.iStockIndexService.queryTransIndex(indexId);
    }

    @RequestMapping({"querySingleIndex.do"})
    @ResponseBody
    public ServerResponse querySingleIndex(@RequestParam("indexCode") String indexCode) {
        MarketVO marketVO = this.iStockIndexService.querySingleIndex(indexCode);
        return ServerResponse.createBySuccess(marketVO);
    }
}
