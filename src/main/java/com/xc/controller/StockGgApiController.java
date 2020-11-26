package com.xc.controller;

import com.xc.common.ServerResponse;
import com.xc.service.IStockGgService;
import com.xc.service.IStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping({"/api/stockGg/"})
public class StockGgApiController {
    private static final Logger log = LoggerFactory.getLogger(StockGgApiController.class);

    @Autowired
    IStockGgService iStockService;


    //查询官网PC端交易 所有股票信息及指定股票信息
    @RequestMapping({"getStock.do"})
    @ResponseBody
    public ServerResponse getStock(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "stockPlate", required = false) String stockPlate, @RequestParam(value = "stockType", required = false) String stockType, @RequestParam(value = "keyWords", required = false) String keyWords, HttpServletRequest request) {
        return this.iStockService.getStockGg(pageNum, pageSize, keyWords, stockPlate, stockType, request);
    }

    //通过股票代码查询股票信息
    @RequestMapping({"getSingleStock.do"})
    @ResponseBody
    public ServerResponse getSingleStock(@RequestParam("code") String code) {
        return this.iStockService.getSingleStock(code);
    }


}
