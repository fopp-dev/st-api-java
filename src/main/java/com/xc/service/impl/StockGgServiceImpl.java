package com.xc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xc.common.ServerResponse;
import com.xc.config.StockPoll;
import com.xc.dao.RealTimeMapper;
import com.xc.dao.StockGgMapper;
import com.xc.dao.StockMapper;
import com.xc.pojo.*;
import com.xc.service.*;
import com.xc.utils.HttpClientRequest;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.stock.pinyin.GetPyByChinese;
import com.xc.utils.stock.qq.QqStockApi;
import com.xc.utils.stock.sina.SinaStockApi;
import com.xc.vo.stock.*;
import com.xc.vo.stock.k.MinDataVO;
import com.xc.vo.stock.k.echarts.EchartsDataVO;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("iStockGgService")
public class StockGgServiceImpl implements IStockGgService {

    private static final Logger log = LoggerFactory.getLogger(StockGgServiceImpl.class);

    @Autowired
    StockGgMapper stockMapper;

    @Autowired
    RealTimeMapper realTimeMapper;

    @Autowired
    IStockMarketsDayService iStockMarketsDayService;

    @Autowired
    StockPoll stockPoll;

    @Autowired
    IUserService iUserService;

    @Autowired
    IStockOptionService iStockOptionService;

    @Autowired
    IStockService iStockService;

    public ServerResponse getSingleStock(String code) {
        if (StringUtils.isBlank(code))
            return ServerResponse.createByErrorMsg("");
//        StockGg stock = new StockGg();
//        stock = this.stockMapper.findStockByCode(code);
        List<StockGg> stocks = new ArrayList<>();
        StockGg stock = new StockGg();
        stocks = this.stockMapper.findStockByCode(code);

        if(stocks == null|| stocks.size() == 0){
            return ServerResponse.createByErrorMsg("");
        }
        stock = stocks.get(0);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        String gid = stock.getStockGid();
        String sinaResult = SinaStockApi.getSinaStock(gid);
        StockVO stockVO = new StockVO();
        if(code.contains("hf")){
            stockVO = SinaStockApi.assembleStockFuturesVO(sinaResult);
        }else {
            stockVO = SinaStockApi.assembleStockVO(sinaResult);
        }
        stockVO.setId(stock.getId().intValue());
        stockVO.setCode(stock.getStockCode());
        stockVO.setSpell(stock.getStockSpell());
        stockVO.setGid(stock.getStockGid());
        //通过接口查询股票信息
//        stockVO.setMinImg(PropertiesUtil.getProperty("sina.single.stock.min.url") + stock.getStockGid() + ".jpg");
//        stockVO.setDayImg(PropertiesUtil.getProperty("sina.single.stock.day.url") + stock.getStockGid() + ".jpg");
//        stockVO.setWeekImg(PropertiesUtil.getProperty("sina.single.stock.week.url") + stock.getStockGid() + ".jpg");
//        stockVO.setMonthImg(PropertiesUtil.getProperty("sina.single.stock.month.url") + stock.getStockGid() + ".jpg");
        return ServerResponse.createBySuccess(stockVO);
    }

    public ServerResponse getStockGg(int pageNum, int pageSize, String keyWords, String stockPlate, String stockType, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        User user = iUserService.getCurrentUser(request);
        List<StockGg> stockList = this.stockMapper.findStockListByKeyWords(keyWords, stockPlate, stockType, Integer.valueOf(0));
        List<StockListVO> stockListVOS = Lists.newArrayList();
        if (stockList.size() > 0) {
            for (StockGg stock : stockList) {
//                StockListVO stockListVO = SinaStockApi.assembleStockListVO(SinaStockApi.getSinaStock(stock.getStockGid()));
                StockListVO stockListVO = new StockListVO();
                ServerResponse stockhjson = iStockService.getGGSingleStock(stock.getStockCode());
                JSONObject data = (JSONObject) stockhjson.getData();
                if(data != null){
                    stockListVO.setCode(stock.getStockCode());
                    stockListVO.setSpell(stock.getStockSpell());
                    stockListVO.setGid(stock.getStockGid());
                    BigDecimal day3Rate = (BigDecimal)selectRateByDaysAndStockGgCode(stock.getStockCode(), 3).getData();
                    stockListVO.setDay3Rate(day3Rate);
                    stockListVO.setStock_plate(stock.getStockPlate());
                    stockListVO.setStock_type(stock.getStockType());
                    stockListVO.setNowPrice((String.valueOf(data.get("nowPrice") != null ? data.get("nowPrice"):0)));
                    stockListVO.setHcrate(new BigDecimal(String.valueOf(data.get("hcrate") != null?data.get("hcrate"):0)));
                    stockListVO.setName(stock.getStockName());
                    stockListVO.setPreclose_px((String.valueOf(data.get("preclose_px") != null ?data.get("preclose_px"):0)));
                }
                //是否添加自选
                if(user == null){
                    stockListVO.setIsOption("0");
                } else {
                    stockListVO.setIsOption(iStockOptionService.isMyOption(user.getId(), stock.getStockCode()));
                }
                stockListVOS.add(stockListVO);
            }
        }
        PageInfo pageInfo = new PageInfo(stockList);
        pageInfo.setList(stockListVOS);
        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse<StockGg> findStockGgByName(String name) {
        return ServerResponse.createBySuccess(this.stockMapper.findStockByName(name));
    }

    public ServerResponse<StockGg> findStockGgByCode(String code) {
        return ServerResponse.createBySuccess(this.stockMapper.findStockByCode(code).get(0));
    }

    public ServerResponse<StockGg> findStockGgById(Integer stockId) {
        return ServerResponse.createBySuccess(this.stockMapper.selectByPrimaryKey(stockId));
    }

    public ServerResponse<PageInfo> listByAdmin(Integer showState, Integer lockState, String code, String name, String stockPlate, String stockType, int pageNum, int pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);
        List<StockGg> stockList = this.stockMapper.listByAdmin(showState, lockState, code, name, stockPlate, stockType);
        List<StockAdminListVO> stockAdminListVOS = Lists.newArrayList();
        for (StockGg stock : stockList) {
            StockAdminListVO stockAdminListVO = assembleStockAdminListVO(stock);
            stockAdminListVOS.add(stockAdminListVO);
        }
        PageInfo pageInfo = new PageInfo(stockList);
        pageInfo.setList(stockAdminListVOS);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private StockAdminListVO assembleStockAdminListVO(StockGg stock) {
        StockAdminListVO stockAdminListVO = new StockAdminListVO();
        stockAdminListVO.setId(stock.getId());
        stockAdminListVO.setStockName(stock.getStockName());
        stockAdminListVO.setStockCode(stock.getStockCode());
        stockAdminListVO.setStockSpell(stock.getStockSpell());
        stockAdminListVO.setStockType(stock.getStockType());
        stockAdminListVO.setStockGid(stock.getStockGid());
        stockAdminListVO.setStockPlate(stock.getStockPlate());
        stockAdminListVO.setIsLock(stock.getIsLock());
        stockAdminListVO.setIsShow(stock.getIsShow());
        stockAdminListVO.setAddTime(stock.getAddTime());
//        StockListVO stockListVO = SinaStockApi.assembleStockListVO(SinaStockApi.getSinaStock(stock.getStockGid()));
        ServerResponse ggSingleStock = iStockService.getGGSingleStock(stock.getStockCode());
        com.alibaba.fastjson.JSONObject result = (com.alibaba.fastjson.JSONObject)ggSingleStock.getData();
        String nowPrice = String.valueOf(result.get("nowPrice"));
        String hcrate = String.valueOf(result.get("hcrate")) ;

        stockAdminListVO.setNowPrice(nowPrice);
        stockAdminListVO.setHcrate(new BigDecimal(hcrate));
        stockAdminListVO.setSpreadRate(stock.getSpreadRate());
        ServerResponse serverResponse = selectRateByDaysAndStockGgCode(stock.getStockCode(), 3);
        BigDecimal day3Rate = new BigDecimal("0");
        if (serverResponse.isSuccess())
            day3Rate = (BigDecimal)serverResponse.getData();
        stockAdminListVO.setDay3Rate(day3Rate);
        return stockAdminListVO;
    }

    public ServerResponse updateLock(Integer stockId) {
        StockGg stock = this.stockMapper.selectByPrimaryKey(stockId);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        if (stock.getIsLock().intValue() == 1) {
            stock.setIsLock(Integer.valueOf(0));
        } else {
            stock.setIsLock(Integer.valueOf(1));
        }
        int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
        if (updateCount > 0)
            return ServerResponse.createBySuccessMsg("");
        return ServerResponse.createByErrorMsg("");
    }

    public ServerResponse updateShow(Integer stockId) {
        StockGg stock = this.stockMapper.selectByPrimaryKey(stockId);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        if (stock.getIsShow().intValue() == 0) {
            stock.setIsShow(Integer.valueOf(1));
        } else {
            stock.setIsShow(Integer.valueOf(0));
        }
        int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
        if (updateCount > 0)
            return ServerResponse.createBySuccessMsg("");
        return ServerResponse.createByErrorMsg("");
    }

    public ServerResponse addStockGg(String stockName, String stockCode, String stockType, String stockPlate, Integer isLock, Integer isShow) {
        if (StringUtils.isBlank(stockName) || StringUtils.isBlank(stockCode) || StringUtils.isBlank(stockType) || isLock == null || isShow == null)
            return ServerResponse.createByErrorMsg("");
        StockGg cstock = (StockGg)findStockGgByCode(stockCode).getData();
        if (cstock != null)
            return ServerResponse.createByErrorMsg("");
        StockGg nstock = (StockGg)findStockGgByName(stockName).getData();
        if (nstock != null)
            return ServerResponse.createByErrorMsg("");
        StockGg stock = new StockGg();
        stock.setStockName(stockName);
        stock.setStockCode(stockCode);
        stock.setStockSpell(GetPyByChinese.converterToFirstSpell(stockName));
        stock.setStockType(stockType);
        stock.setStockGid(stockType + stockCode);
        stock.setIsLock(isLock);
        stock.setIsShow(isShow);
        stock.setAddTime(new Date());
        if (stockPlate != null)
            stock.setStockPlate(stockPlate);
        int insertCount = this.stockMapper.insert(stock);
        if (insertCount > 0)
            return ServerResponse.createBySuccessMsg("");
        return ServerResponse.createByErrorMsg("");
    }

    public int CountStockGgNum() {
        return this.stockMapper.CountStockNum();
    }

    public int CountShowNum(Integer showState) {
        return this.stockMapper.CountShowNum(showState);
    }

    public int CountUnLockNum(Integer lockState) {
        return this.stockMapper.CountUnLockNum(lockState);
    }

    public List findStockGgList() {
        return this.stockMapper.findStockList();
    }

    public ServerResponse selectRateByDaysAndStockGgCode(String stockCode, int days) {
//        StockGg stock = this.stockMapper.findStockByCode(stockCode);
        List<StockGg> stocks = new ArrayList<>();
        StockGg stock = new StockGg();
        stocks = this.stockMapper.findStockByCode(stockCode);

        if(stocks == null|| stocks.size() == 0){
            return ServerResponse.createByErrorMsg("");
        }
        stock = stocks.get(0);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        BigDecimal daysRate = this.iStockMarketsDayService.selectRateByDaysAndStockCode(stock.getId(), days);
        return ServerResponse.createBySuccess(daysRate);
    }



    public ServerResponse updateStockGg(StockGg model) {
        if (StringUtils.isBlank(model.getId().toString()) || StringUtils.isBlank(model.getStockName()))
            return ServerResponse.createByErrorMsg("");
        StockGg stock = this.stockMapper.selectByPrimaryKey(model.getId());
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        stock.setStockName(model.getStockName());
        if (model.getSpreadRate() != null)
            stock.setSpreadRate(model.getSpreadRate());
        int updateCount = this.stockMapper.updateByPrimaryKeySelective(stock);
        if (updateCount > 0)
            return ServerResponse.createBySuccessMsg("");
        return ServerResponse.createByErrorMsg("");
    }
}
