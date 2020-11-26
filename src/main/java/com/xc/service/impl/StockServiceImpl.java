package com.xc.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xc.common.ServerResponse;
import com.xc.config.StockPoll;
import com.xc.dao.*;
import com.xc.pojo.*;
import com.xc.service.IStockMarketsDayService;
import com.xc.service.IStockOptionService;
import com.xc.service.IStockService;
import com.xc.service.IUserService;
import com.xc.utils.HttpClientRequest;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.stock.ali.DataUtil;
import com.xc.utils.stock.pinyin.GetPyByChinese;
import com.xc.utils.stock.qq.QqStockApi;
import com.xc.utils.stock.sina.SinaStockApi;
import com.xc.vo.stock.MarketVO;
import com.xc.vo.stock.MarketVOResult;
import com.xc.vo.stock.StockAdminListVO;
import com.xc.vo.stock.StockListVO;
import com.xc.vo.stock.StockVO;
import com.xc.vo.stock.k.MinDataVO;
import com.xc.vo.stock.k.echarts.EchartsDataVO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iStockService")
public class StockServiceImpl implements IStockService {
  private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

  @Autowired
  StockMapper stockMapper;

    @Autowired
    StockGgMapper stockGgMapper;

  @Autowired
  RealTimeMapper realTimeMapper;

  @Autowired
  IStockMarketsDayService iStockMarketsDayService;

  @Autowired
  StockPoll stockPoll;

  @Autowired
  StockFuturesMapper stockFuturesMapper;

  @Autowired
  StockIndexMapper stockIndexMapper;

  @Autowired
  IUserService iUserService;

  @Autowired
  IStockOptionService iStockOptionService;

  public ServerResponse getMarket() {
    String market_url = PropertiesUtil.getProperty("sina.market.url");
    String result = null;
    try {
      result = HttpClientRequest.doGet(market_url);
    } catch (Exception e) {
      log.error("e = {}", e);
    }
    String[] marketArray = result.split(";");
    List<MarketVO> marketVOS = Lists.newArrayList();
    for (int i = 0; i < marketArray.length; i++) {
      String hqstr = marketArray[i];
      try {
        if (StringUtils.isNotBlank(hqstr)) {
          hqstr = hqstr.substring(hqstr.indexOf("\"") + 1, hqstr.lastIndexOf("\""));
          MarketVO marketVO = new MarketVO();
          String[] sh01_arr = hqstr.split(",");
          marketVO.setName(sh01_arr[0]);
          marketVO.setNowPrice(sh01_arr[1]);
          marketVO.setIncrease(sh01_arr[2]);
          marketVO.setIncreaseRate(sh01_arr[3]);
          marketVOS.add(marketVO);
        }
      } catch (Exception e) {
        log.error("str = {} ,  e = {}", hqstr, e);
      }
    }
    MarketVOResult marketVOResult = new MarketVOResult();
    marketVOResult.setMarket(marketVOS);
    return ServerResponse.createBySuccess(marketVOResult);
  }

  public ServerResponse getStock(int pageNum, int pageSize, String keyWords, String stockPlate, String stockType, HttpServletRequest request) {
    PageHelper.startPage(pageNum, pageSize);
    User user = iUserService.getCurrentUser(request);
    List<Stock> stockList = this.stockMapper.findStockListByKeyWords(keyWords, stockPlate, stockType, Integer.valueOf(0));
    List<StockListVO> stockListVOS = Lists.newArrayList();
    if (stockList.size() > 0)
      for (Stock stock : stockList) {
        StockListVO stockListVO = SinaStockApi.assembleStockListVO(SinaStockApi.getSinaStock(stock.getStockGid()));
        stockListVO.setCode(stock.getStockCode());
        stockListVO.setSpell(stock.getStockSpell());
        stockListVO.setGid(stock.getStockGid());
        BigDecimal day3Rate = (BigDecimal)selectRateByDaysAndStockCode(stock.getStockCode(), 3).getData();
        stockListVO.setDay3Rate(day3Rate);
        stockListVO.setStock_plate(stock.getStockPlate());
        stockListVO.setStock_type(stock.getStockType());
        //是否添加自选
        if(user == null){
          stockListVO.setIsOption("0");
        } else {
          stockListVO.setIsOption(iStockOptionService.isMyOption(user.getId(), stock.getStockCode()));
        }
        stockListVOS.add(stockListVO);
      }
    PageInfo pageInfo = new PageInfo(stockList);
    pageInfo.setList(stockListVOS);
    return ServerResponse.createBySuccess(pageInfo);
  }

  public void z1() {
    this.stockPoll.z1();
  }
  public void z11() {
    this.stockPoll.z11();
  }
  public void z12() {
    this.stockPoll.z12();
  }

  public void z2() {
    this.stockPoll.z2();
  }
  public void z21() {
    this.stockPoll.z21();
  }
  public void z22() {
    this.stockPoll.z22();
  }

  public void z3() {
    this.stockPoll.z3();
  }
  public void z31() {
    this.stockPoll.z31();
  }
  public void z32() {
    this.stockPoll.z32();
  }

  public void z4() {
    this.stockPoll.z4();
  }
  public void z41() {
    this.stockPoll.z41();
  }
  public void z42() {
    this.stockPoll.z42();
  }

  public void h1() {
    this.stockPoll.h1();
  }
  public void h11() {
    this.stockPoll.h11();
  }
  public void h12() {
    this.stockPoll.h12();
  }

  public void h2() {
    this.stockPoll.h2();
  }
  public void h21() {
    this.stockPoll.h21();
  }
  public void h22() {
    this.stockPoll.h22();
  }

  public void h3() {
    this.stockPoll.h3();
  }
  public void h31() {
    this.stockPoll.h31();
  }
  public void h32() {
    this.stockPoll.h32();
  }

  public ServerResponse getDateline(HttpServletResponse response, String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    response.setHeader("Access-Control-Allow-Origin", "*");
    Date time = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    String end = sdf.format(time);
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.add(2, -3);
    Date m = c.getTime();
    String mon = sdf.format(m);
    String methodUrl = "http://q.stock.sohu.com/hisHq?code=cn_" + code + "+&start=" + mon + "&end=" + end + "&stat=1&order=D";
    HttpURLConnection connection = null;
    BufferedReader reader = null;
    String line = null;
    EchartsDataVO echartsDataVO = new EchartsDataVO();
    try {
      URL url = new URL(methodUrl);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("GET");
      connection.connect();
      if (connection.getResponseCode() == 200) {
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null)
          result.append(line).append(System.getProperty("line.separator"));
        JSONArray jsonArray = JSONArray.fromObject(result.toString());
        JSONObject json = jsonArray.getJSONObject(0);
        JSONArray jsonArray1 = JSONArray.fromObject(json.get("hq"));
        Collections.reverse((List<?>)jsonArray1);
        double[][] values = (double[][])null;
        Object[][] volumes = (Object[][])null;
        String[] date = null;
        values = new double[jsonArray1.size()][5];
        volumes = new Object[jsonArray1.size()][3];
        date = new String[jsonArray1.size()];
        for (int i = 0; i < jsonArray1.size(); i++) {
          JSONArray js = JSONArray.fromObject(jsonArray1.get(i));
          date[i] = js.get(0).toString();
          values[i][0] = Double.valueOf(js.get(1).toString()).doubleValue();
          values[i][1] = Double.valueOf(js.get(2).toString()).doubleValue();
          values[i][2] = Double.valueOf(js.get(5).toString()).doubleValue();
          values[i][3] = Double.valueOf(js.get(6).toString()).doubleValue();
          values[i][4] = Double.valueOf(js.get(7).toString()).doubleValue();
          volumes[i][0] = Integer.valueOf(i);
          volumes[i][1] = Double.valueOf(js.get(7).toString());
          volumes[i][2] = Integer.valueOf((Double.valueOf(js.get(3).toString()).doubleValue() > 0.0D) ? 1 : -1);
        }
        echartsDataVO.setDate(date);
        echartsDataVO.setValues(values);
        echartsDataVO.setVolumes(volumes);
        echartsDataVO.setStockCode(stock.getStockCode());
        echartsDataVO.setStockName(stock.getStockName());
        log.info(String.valueOf(echartsDataVO));
        ServerResponse.createBySuccess(echartsDataVO);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      connection.disconnect();
    }
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  public ServerResponse getSingleStock(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = new Stock();
    Integer depositAmt = 0;
    //期货
    if(code.contains("hf")){
      StockFutures futmodel = stockFuturesMapper.selectFuturesByCode(code.replace("hf_",""));
      stock.setStockGid(futmodel.getFuturesGid());
      stock.setStockCode(futmodel.getFuturesCode());
      stock.setStockName(futmodel.getFuturesName());
      stock.setAddTime(futmodel.getAddTime());
      stock.setId(futmodel.getId());
      stock.setStockSpell("0");
      depositAmt = futmodel.getDepositAmt();
    } else if(code.contains("sh") || code.contains("sz")){ //指数
      StockIndex model = stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
      stock.setStockGid(model.getIndexGid());
      stock.setStockCode(model.getIndexCode());
      stock.setStockName(model.getIndexName());
      stock.setAddTime(model.getAddTime());
      stock.setId(model.getId());
      stock.setStockSpell("0");
      depositAmt = model.getDepositAmt();
    } else {//股票
      stock = this.stockMapper.findStockByCode(code);
    }

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
    stockVO.setDepositAmt(depositAmt);
    stockVO.setId(stock.getId().intValue());
    stockVO.setCode(stock.getStockCode());
    stockVO.setSpell(stock.getStockSpell());
    stockVO.setGid(stock.getStockGid());
    stockVO.setStockType(stock.getStockType());
    stockVO.setMinImg(PropertiesUtil.getProperty("sina.single.stock.min.url") + stock.getStockGid() + ".jpg");
    stockVO.setDayImg(PropertiesUtil.getProperty("sina.single.stock.day.url") + stock.getStockGid() + ".jpg");
    stockVO.setWeekImg(PropertiesUtil.getProperty("sina.single.stock.week.url") + stock.getStockGid() + ".jpg");
    stockVO.setMonthImg(PropertiesUtil.getProperty("sina.single.stock.month.url") + stock.getStockGid() + ".jpg");
    return ServerResponse.createBySuccess(stockVO);
  }


  public ServerResponse getMinK(String code, Integer time, Integer ma, Integer size) {
    if (StringUtils.isBlank(code) || time == null || ma == null || size == null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    return SinaStockApi.getStockMinK(stock, time.intValue(), ma.intValue(), size.intValue());
  }

  public ServerResponse getMinK_Echarts(String code, Integer time, Integer ma, Integer size) {
    if (StringUtils.isBlank(code) || time == null || ma == null || size == null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getStockMinK(stock, time.intValue(), ma.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

    public ServerResponse getGGMinK_Echarts(String code, Integer time,  Integer size) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("stockCode",code);
        // time 1M,5M,10M,15M,30M,1H,D
        String stime = "5M";
        switch (time){
            case 5:
                stime = "5M";
                break;
            case 15:
                stime = "15M";
                break;
            case 30:
                stime = "30M";
                break;
            case 60:
                stime = "1H";
                break;
            case 999:
                stime = "D";
                break;
        }
        List<StockGg> stocks = new ArrayList<>();
        StockGg stock = new StockGg();
        stocks = this.stockGgMapper.findStockByCode(code);

        if(stocks == null|| stocks.size() == 0){
            return ServerResponse.createByErrorMsg("");
        }
        stock = stocks.get(0);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        try {
            result.put("stockName",stock.getStockName());
            String kmsret = DataUtil.GetKMaps(stock.getStockType()+code, stime,size);
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(kmsret);
            com.alibaba.fastjson.JSONArray jsonArray = jsonObject.getJSONArray("Obj");
            List<List<Double>> values = new ArrayList<>();
            List<List<Integer>> volumes = new ArrayList<>();
            List<List<String>> date = new ArrayList<>();
            Double shoupan = 0.0;
            for (int i = jsonArray.size() -1; i >=0 ; i--) {
                com.alibaba.fastjson.JSONObject obj = jsonArray.getJSONObject(i);
                List<Double> value = new ArrayList<>();
                List<Integer> volume = new ArrayList<>();
                List<String> da = new ArrayList<>();
                volume.add(i);
                volume.add(obj.getInteger("V"));
                if (obj.getDouble("C") > shoupan) {
                    volume.add(1);
                } else {
                    volume.add(-1);
                }
                value.add(obj.getDouble("O"));
                value.add(obj.getDouble("C"));
                shoupan = obj.getDouble("C");
                value.add(obj.getDouble("L"));
                value.add(obj.getDouble("H"));
                value.add(obj.getDouble("V"));


                da.add(obj.getString("D"));

                values.add(value);
                volumes.add(volume);
                date.add(da);
            }
            result.put("values", values);
            result.put("volumes", volumes);
            result.put("date", date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ServerResponse.createBySuccess(result);
    }
    public ServerResponse getGGSingleStock(String code) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();

        if (StringUtils.isBlank(code))
            return ServerResponse.createByErrorMsg("");
        List<StockGg> stocks = new ArrayList<>();
        StockGg stock = new StockGg();
        stocks = this.stockGgMapper.findStockByCode(code);

        if(stocks == null|| stocks.size() == 0){
            return ServerResponse.createByErrorMsg("");
        }
        stock = stocks.get(0);
        if (stock == null)
            return ServerResponse.createByErrorMsg("");
        String kmsret = "";
        try {
            kmsret = DataUtil.GetcomMaps(stock.getStockType()+code);
        }catch (Exception e){
            e.printStackTrace();
        }
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(kmsret);
        if(jsonObject != null){
            com.alibaba.fastjson.JSONObject obj = jsonObject.getJSONObject("Obj");
            result.put("id", stock.getId());
            result.put("name", obj.getString("N"));
            result.put("code", obj.getString("C"));
            result.put("spell", stock.getStockSpell());
            result.put("gid", stock.getStockGid());
            result.put("nowPrice", obj.getDouble("P"));
            result.put("hcrate",  obj.getDouble("VF"));
            result.put("today_max",  obj.getDouble("H"));
            result.put("today_min", obj.getDouble("L"));
            result.put("business_balance", obj.getDouble("A"));
            result.put("business_amount", obj.getDouble("V"));
            result.put("preclose_px",  obj.getDouble("YC"));
            result.put("open_px",  obj.getDouble("O"));
            result.put("buy1",  obj.getDouble("B1"));
            result.put("buy2",  obj.getDouble("B2"));
            result.put("buy3",  obj.getDouble("B3"));
            result.put("buy4",  obj.getDouble("B4"));
            result.put("buy5",  obj.getDouble("B5"));
            result.put("sell1",  obj.getDouble("S1"));
            result.put("sell2",  obj.getDouble("S2"));
            result.put("sell3",  obj.getDouble("S3"));
            result.put("sell4",  obj.getDouble("S4"));
            result.put("sell5",  obj.getDouble("S5"));
            result.put("buy1_num", obj.getInteger("B1V"));
            result.put("buy2_num", obj.getInteger("B2V"));
            result.put("buy3_num", obj.getInteger("B3V"));
            result.put("buy4_num", obj.getInteger("B4V"));
            result.put("buy5_num", obj.getInteger("B5V"));
            result.put("sell1_num", obj.getInteger("S1V"));
            result.put("sell2_num", obj.getInteger("S2V"));
            result.put("sell3_num", obj.getInteger("S3V"));
            result.put("sell4_num", obj.getInteger("S4V"));
            result.put("sell5_num", obj.getInteger("S5V"));
            result.put("depositAmt", 0);
            result.put("stockType",stock.getStockType());
        }
        return ServerResponse.createBySuccess(result);
    }
  public ServerResponse findStock(String code) {
      List<StockGg> stocks = new ArrayList<>();
      StockGg stock = new StockGg();
      stocks = this.stockGgMapper.findStockByCode(code);

      if(stocks == null|| stocks.size() == 0){
          return ServerResponse.createByErrorMsg("");
      }
      stock = stocks.get(0);
      if (stock == null)
          return ServerResponse.createByErrorMsg("");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String date = simpleDateFormat.format(new Date());
    if (LocalDateTime.now().getHour() < 9)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,-24);
        date=simpleDateFormat.format(calendar.getTime());
    }

    String kmsret = "";
    try {
      kmsret = DataUtil.GetLstKM4Maps(stock.getStockType()+code, "1M",date);
    }catch (Exception e){
      e.printStackTrace();
    }
    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(kmsret);
    com.alibaba.fastjson.JSONArray jsonArray = jsonObject.getJSONArray("Obj");
    Map<String, Object> map = new HashMap<>();
    List<Double> price = new ArrayList();
    List<Double> averagePrice = new ArrayList();
    List<Double> rates = new ArrayList();
    List<String> time = new ArrayList();
    List<Integer> volumes = new ArrayList();
    List<Integer> amounts = new ArrayList();
      String kmsret2 = "";
      try {
          kmsret2 = DataUtil.GetcomMaps(stock.getStockType()+code);
      }catch (Exception e){
          e.printStackTrace();
      }
      com.alibaba.fastjson.JSONObject jsonObject2 = JSON.parseObject(kmsret2);
      com.alibaba.fastjson.JSONObject obj2 = jsonObject2.getJSONObject("Obj");
      BigDecimal firstprice = obj2.getBigDecimal("YC");

    for (int i = 0; i < jsonArray.size(); i++) {
      com.alibaba.fastjson.JSONObject obj = jsonArray.getJSONObject(i);

      amounts.add(obj.getInteger("V"));//交易量
      price.add(obj.getDouble("C")); //现价

        BigDecimal cha = obj.getBigDecimal("C").subtract(firstprice);
        BigDecimal rate = cha.divide(firstprice,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        double f1 = rate.doubleValue();
        rates.add(f1);//涨幅

      volumes.add(obj.getInteger("A"));//成交额
      long timelong = obj.getLong("Tick");
      SimpleDateFormat sdr = new SimpleDateFormat("HH:mm");
      String timeString = sdr.format(new Date(timelong * 1000L));
      time.add(timeString);
    }
    map.put("stockCode", code);
    map.put("size", Integer.valueOf(jsonArray.size()));
    map.put("time", time);
    map.put("volumes", volumes);
    map.put("price", price);
    map.put("averagePrice", averagePrice);
    map.put("rates", rates);
    map.put("amounts", amounts);
    return ServerResponse.createBySuccess(map);
  }
  /*期货分时-k线*/
  public ServerResponse getFuturesMinK_Echarts(String code, Integer time, Integer size) {
    if (StringUtils.isBlank(code) || time == null)
      return ServerResponse.createByErrorMsg("");
    StockFutures stock = this.stockFuturesMapper.selectFuturesByCode(code.split("_")[1]);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getFuturesMinK(stock, time.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*指数分时-k线*/
  public ServerResponse getIndexMinK_Echarts(String code, Integer time, Integer size) {
    if (StringUtils.isBlank(code) || time == null)
      return ServerResponse.createByErrorMsg("");
    StockIndex stock = this.stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = SinaStockApi.getIndexMinK(stock, time.intValue(), size.intValue());
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*股票日线-K线*/
  public ServerResponse getDayK_Echarts(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.findStockByCode(code);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getGpStockDayK(stock);
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*期货日线-K线*/
  public ServerResponse getFuturesDayK(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    StockFutures stock = this.stockFuturesMapper.selectFuturesByCode(code.split("_")[1]);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getQqStockDayK(stock);
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  /*指数日线-K线*/
  public ServerResponse getIndexDayK(String code) {
    if (StringUtils.isBlank(code))
      return ServerResponse.createByErrorMsg("");
    StockIndex stock = this.stockIndexMapper.selectIndexByCode(code.replace("sh","").replace("sz",""));
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    ServerResponse<MinDataVO> serverResponse = QqStockApi.getQqIndexDayK(stock);
    MinDataVO minDataVO = (MinDataVO)serverResponse.getData();
    EchartsDataVO echartsDataVO = SinaStockApi.assembleEchartsDataVO(minDataVO);
    return ServerResponse.createBySuccess(echartsDataVO);
  }

  public ServerResponse<Stock> findStockByName(String name) {
    return ServerResponse.createBySuccess(this.stockMapper.findStockByName(name));
  }

  public ServerResponse<Stock> findStockByCode(String code) {
    return ServerResponse.createBySuccess(this.stockMapper.findStockByCode(code));
  }

  public ServerResponse<Stock> findStockById(Integer stockId) {
    return ServerResponse.createBySuccess(this.stockMapper.selectByPrimaryKey(stockId));
  }

  public ServerResponse<PageInfo> listByAdmin(Integer showState, Integer lockState, String code, String name, String stockPlate, String stockType, int pageNum, int pageSize, HttpServletRequest request) {
    PageHelper.startPage(pageNum, pageSize);
    List<Stock> stockList = this.stockMapper.listByAdmin(showState, lockState, code, name, stockPlate, stockType);
    List<StockAdminListVO> stockAdminListVOS = Lists.newArrayList();
    for (Stock stock : stockList) {
      StockAdminListVO stockAdminListVO = assembleStockAdminListVO(stock);
      stockAdminListVOS.add(stockAdminListVO);
    }
    PageInfo pageInfo = new PageInfo(stockList);
    pageInfo.setList(stockAdminListVOS);
    return ServerResponse.createBySuccess(pageInfo);
  }

  private StockAdminListVO assembleStockAdminListVO(Stock stock) {
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
    StockListVO stockListVO = SinaStockApi.assembleStockListVO(SinaStockApi.getSinaStock(stock.getStockGid()));
    stockAdminListVO.setNowPrice(stockListVO.getNowPrice());
    stockAdminListVO.setHcrate(stockListVO.getHcrate());
    stockAdminListVO.setSpreadRate(stock.getSpreadRate());
    ServerResponse serverResponse = selectRateByDaysAndStockCode(stock.getStockCode(), 3);
    BigDecimal day3Rate = new BigDecimal("0");
    if (serverResponse.isSuccess())
      day3Rate = (BigDecimal)serverResponse.getData();
    stockAdminListVO.setDay3Rate(day3Rate);
    return stockAdminListVO;
  }

  public ServerResponse updateLock(Integer stockId) {
    Stock stock = this.stockMapper.selectByPrimaryKey(stockId);
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
    Stock stock = this.stockMapper.selectByPrimaryKey(stockId);
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

  public ServerResponse addStock(String stockName, String stockCode, String stockType, String stockPlate, Integer isLock, Integer isShow) {
    if (StringUtils.isBlank(stockName) || StringUtils.isBlank(stockCode) || StringUtils.isBlank(stockType) || isLock == null || isShow == null)
      return ServerResponse.createByErrorMsg("");
    Stock cstock = (Stock)findStockByCode(stockCode).getData();
    if (cstock != null)
      return ServerResponse.createByErrorMsg("");
    Stock nstock = (Stock)findStockByName(stockName).getData();
    if (nstock != null)
      return ServerResponse.createByErrorMsg("");
    Stock stock = new Stock();
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

  public int CountStockNum() {
    return this.stockMapper.CountStockNum();
  }

  public int CountShowNum(Integer showState) {
    return this.stockMapper.CountShowNum(showState);
  }

  public int CountUnLockNum(Integer lockState) {
    return this.stockMapper.CountUnLockNum(lockState);
  }

  public List findStockList() {
    return this.stockMapper.findStockList();
  }

  public ServerResponse selectRateByDaysAndStockCode(String stockCode, int days) {
    Stock stock = this.stockMapper.findStockByCode(stockCode);
    if (stock == null)
      return ServerResponse.createByErrorMsg("");
    BigDecimal daysRate = this.iStockMarketsDayService.selectRateByDaysAndStockCode(stock.getId(), days);
    return ServerResponse.createBySuccess(daysRate);
  }

  public ServerResponse updateStock(Stock model) {
    if (StringUtils.isBlank(model.getId().toString()) || StringUtils.isBlank(model.getStockName()))
      return ServerResponse.createByErrorMsg("");
    Stock stock = this.stockMapper.selectByPrimaryKey(model.getId());
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
