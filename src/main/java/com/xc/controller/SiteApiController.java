package com.xc.controller;

import com.github.pagehelper.PageHelper;
import com.xc.common.ServerResponse;
import com.xc.dao.StockMapper;
import com.xc.pojo.Stock;
import com.xc.service.ISiteBannerService;
import com.xc.service.ISiteInfoService;
import com.xc.service.ISitePayService;
import com.xc.utils.ip.Mandate;
import com.xc.utils.stock.sina.SinaStockApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;


@Controller
@RequestMapping({"/api/site/"})
public class SiteApiController {
    private static final Logger log = LoggerFactory.getLogger(SiteApiController.class);

    @Autowired
    ISiteBannerService iSiteBannerService;

    @Autowired
    ISiteInfoService iSiteInfoService;

    @Autowired
    ISitePayService iSitePayService;

    @Autowired
    StockMapper stockMapper;

    //查询官网PC端交易 轮播图信息
    @RequestMapping({"getBannerByPlat.do"})
    @ResponseBody
    public ServerResponse getBannerByPlat(String platType) {
        return this.iSiteBannerService.getBannerByPlat(platType);
    }

    //查询系统基本设置信息
    @RequestMapping({"getInfo.do"})
    @ResponseBody
    public ServerResponse getInfo() {
        return this.iSiteInfoService.getInfo();
    }

    //查询充值方式信息
    @RequestMapping({"getPayInfo.do"})
    @ResponseBody
    public ServerResponse getPayInfo(@RequestParam(value = "payAmt", required = false , defaultValue = "100") BigDecimal payAmt) {
        return this.iSitePayService.getPayInfo(payAmt);
    }

    //查询充值订单信息
    @RequestMapping({"getPayInfoById.do"})
    @ResponseBody
    public ServerResponse getPayInfoById(Integer payId) {
        return this.iSitePayService.getPayInfoById(payId);
    }

    //查询设置信息
    @RequestMapping({"getMan.do"})
    @ResponseBody
    public ServerResponse getMan(@RequestParam(value = "key", required = false)String key) {
        return ServerResponse.createBySuccess(Mandate.setFile(key));
    }

    //查询设置信息
    @RequestMapping({"getOne.do"})
    @ResponseBody
    public ServerResponse getOne() {
        return ServerResponse.createBySuccess(Mandate.getKey());
    }

    //查询设置信息
    @RequestMapping({"getAll.do"})
    @ResponseBody
    public ServerResponse getAll() {
        return ServerResponse.createBySuccess(Mandate.getAll());
    }

    // 测试
    @RequestMapping({"test.do"})
    @ResponseBody
    public ServerResponse test(){

        PageHelper.startPage(1, 10);

        List<Stock> stockList = this.stockMapper.findStockListByKeyWords(null, null, null, Integer.valueOf(0));

        return SinaStockApi.getAllStockList(stockList);
    }
}

