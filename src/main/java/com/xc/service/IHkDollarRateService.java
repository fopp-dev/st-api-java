package com.xc.service;

import com.xc.common.ServerResponse;
import com.xc.pojo.HkDollarRate;


public interface IHkDollarRateService {

    HkDollarRate getHkDollarRate();

    ServerResponse update(HkDollarRate paramHkDollarRate);

}
