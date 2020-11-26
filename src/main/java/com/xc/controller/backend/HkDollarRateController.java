package com.xc.controller.backend;

import com.xc.common.ServerResponse;
import com.xc.pojo.HkDollarRate;
import com.xc.service.IHkDollarRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({"/admin/sethkDollarRate/"})
public class HkDollarRateController {
    private static final Logger log = LoggerFactory.getLogger(AdminSiteHksSettingController.class);

    @Autowired
    IHkDollarRateService iHkDollarRateService;


    //查询港元汇率
    @RequestMapping({"gethkRate.do"})
    @ResponseBody
    public ServerResponse gethkRate() {
        return ServerResponse.createBySuccess(this.iHkDollarRateService.getHkDollarRate());
    }


    //修改风控设置 股票风控信息
    @RequestMapping({"update.do"})
    @ResponseBody
    public ServerResponse update(HkDollarRate hkDollarRate) {
        return this.iHkDollarRateService.update(hkDollarRate);
    }
}

