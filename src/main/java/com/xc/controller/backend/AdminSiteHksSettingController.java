package com.xc.controller.backend;


import com.xc.common.ServerResponse;
import com.xc.pojo.SiteHksSetting;
import com.xc.service.ISiteHksSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping({"/admin/setHks/"})
public class AdminSiteHksSettingController {
    private static final Logger log = LoggerFactory.getLogger(AdminSiteHksSettingController.class);

    @Autowired
    ISiteHksSettingService iSiteHksSettingService;

    //修改风控设置 股票风控信息
    @RequestMapping({"update.do"})
    @ResponseBody
    public ServerResponse update(SiteHksSetting siteHksSetting) {
        return this.iSiteHksSettingService.update(siteHksSetting);
    }
}