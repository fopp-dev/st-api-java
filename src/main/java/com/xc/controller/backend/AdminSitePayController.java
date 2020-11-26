package com.xc.controller.backend;


import com.xc.common.ServerResponse;
import com.xc.pojo.SitePay;
import com.xc.service.ISitePayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping({"/admin/pay/"})
public class AdminSitePayController {
    private static final Logger log = LoggerFactory.getLogger(AdminSitePayController.class);

    @Autowired
    ISitePayService iSitePayService;

    //添加系统基本设置 支付渠道设置信息
    @RequestMapping({"add.do"})
    @ResponseBody
    public ServerResponse add(SitePay sitePay) {
        return this.iSitePayService.add(sitePay);
    }

    //修改系统基本设置 支付渠道设置信息
    @RequestMapping({"update.do"})
    @ResponseBody
    public ServerResponse update(SitePay sitePay) {
        return this.iSitePayService.update(sitePay);
    }

    //分页查询系统基本设置 支付渠道设置信息及模糊查询
    @RequestMapping({"list.do"})
    @ResponseBody
    public ServerResponse list(@RequestParam("channelType") String channelType, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return this.iSitePayService.listByAdmin(channelType, pageNum, pageSize);
    }

    //删除系统基本设置 支付渠道设置信息
    @RequestMapping({"del.do"})
    @ResponseBody
    public ServerResponse del(@RequestParam("cId") Integer cId) {
        return this.iSitePayService.del(cId);
    }

}
