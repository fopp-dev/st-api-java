package com.xc.service.impl;

import com.xc.common.ServerResponse;
import com.xc.dao.HkDollarRateMapper;
import com.xc.pojo.HkDollarRate;
import com.xc.pojo.SiteHksSetting;
import com.xc.service.IHkDollarRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("iHkDollarRateService")
public class HkDollarRateServiceImpl implements IHkDollarRateService {

    @Autowired
    private HkDollarRateMapper hkDollarRateMapper;

    @Override
    public HkDollarRate getHkDollarRate() {
        HkDollarRate hkDollarRate = null;

        List list = this.hkDollarRateMapper.findAllHkDollarRate();

        if (list.size() > 0) {

            hkDollarRate = (HkDollarRate) list.get(0);

        }
        return hkDollarRate;
    }

    @Override
    public ServerResponse update(HkDollarRate setting) {
        if (setting.getId() == null) {
            return ServerResponse.createByErrorMsg("ID 不能为空");
        }
        HkDollarRate hkDollarRate = this.hkDollarRateMapper.selectByPrimaryKey(setting.getId());
        if (hkDollarRate == null) {
            return ServerResponse.createByErrorMsg("查不到设置记录");
        }

        int updateCount = this.hkDollarRateMapper.updateByPrimaryKeySelective(setting);

        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }
}
