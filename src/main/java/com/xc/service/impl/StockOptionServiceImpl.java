package com.xc.service.impl;

 import com.alibaba.fastjson.JSON;
 import com.github.pagehelper.PageHelper;

 import com.github.pagehelper.PageInfo;

 import com.google.common.collect.Lists;

 import com.xc.common.ServerResponse;

 import com.xc.dao.StockMapper;

 import com.xc.dao.StockOptionMapper;

 import com.xc.pojo.Stock;

 import com.xc.pojo.StockOption;

 import com.xc.pojo.User;

 import com.xc.service.IStockOptionService;

 import com.xc.service.IUserService;

 import com.xc.utils.stock.ali.DataUtil;
 import com.xc.utils.stock.sina.SinaStockApi;

 import com.xc.vo.stock.StockOptionListVO;

 import com.xc.vo.stock.StockVO;

 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.util.List;

 import javax.servlet.http.HttpServletRequest;

 import org.slf4j.Logger;

 import org.slf4j.LoggerFactory;

 import org.springframework.beans.factory.annotation.Autowired;

 import org.springframework.stereotype.Service;


 @Service("iStockOptionService")

 public class StockOptionServiceImpl implements IStockOptionService {

   private static final Logger log = LoggerFactory.getLogger(StockOptionServiceImpl.class);

   @Autowired
   StockOptionMapper stockOptionMapper;

   @Autowired
   IUserService iUserService;

   @Autowired
   StockMapper stockMapper;

   public ServerResponse<PageInfo> findMyStockOptions(String keyWords, HttpServletRequest request, int pageNum, int pageSize) {

     PageHelper.startPage(pageNum, pageSize);
     User user = this.iUserService.getCurrentUser(request);
     List<StockOption> stockOptions = this.stockOptionMapper.findMyOptionByKeywords(user.getId(), keyWords);

     List<StockOptionListVO> stockOptionListVOS = Lists.newArrayList();
     for (StockOption option : stockOptions) {
       StockOptionListVO stockOptionListVO = assembleStockOptionListVO(option);
       stockOptionListVO.setIsOption("1");
       stockOptionListVOS.add(stockOptionListVO);
     }
     PageInfo pageInfo = new PageInfo(stockOptions);

     pageInfo.setList(stockOptionListVOS);

     return ServerResponse.createBySuccess(pageInfo);

   }

   public ServerResponse isOption(Integer uid, String code) {

     StockOption stockOption = this.stockOptionMapper.isOption(uid, code);

     if (stockOption == null) {

       return ServerResponse.createBySuccessMsg("未添加");

     }

     return ServerResponse.createByErrorMsg("已添加");

   }

     public String isMyOption(Integer uid, String code) {
         StockOption stockOption = this.stockOptionMapper.isOption(uid, code);
         if (stockOption == null) {
             return "0";
         }
         return "1";

     }

   private StockOptionListVO assembleStockOptionListVO(StockOption option) {

         StockOptionListVO stockOptionListVO = new StockOptionListVO();



         stockOptionListVO.setId(option.getId().intValue());

         stockOptionListVO.setStockName(option.getStockName());

         stockOptionListVO.setStockCode(option.getStockCode());

         stockOptionListVO.setStockGid(option.getStockGid());

         StockVO stockVO = new StockVO();
         if(option.getStockGid().contains("hf")){
             stockVO = SinaStockApi.assembleStockFuturesVO(SinaStockApi.getSinaStock(option.getStockGid()));
         }else if(option.getStockGid().contains("HK")) {
             // 港股
             com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
             String kmsret = "";
             try {
                 kmsret = DataUtil.GetcomMaps(option.getStockGid());
             }catch (Exception e){
                 e.printStackTrace();
             }
             com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(kmsret);
             if(jsonObject != null){
                 com.alibaba.fastjson.JSONObject obj = jsonObject.getJSONObject("Obj");

                 stockVO.setName(obj.getString("N"));

                 stockVO.setNowPrice(obj.getDouble("P").toString());

                 stockVO.setToday_max(obj.getDouble("H").toString());

                 stockVO.setToday_min(obj.getDouble("L").toString());

                 stockVO.setBusiness_amount(obj.getDouble("V").toString());

                 stockVO.setBusiness_balance(obj.getDouble("A").toString());

                 stockVO.setPreclose_px(obj.getDouble("YC").toString());

                 stockVO.setOpen_px(obj.getDouble("O").toString());

                 BigDecimal chang_rate = new BigDecimal(obj.getDouble("VF").toString());

                 stockVO.setHcrate(chang_rate);
             }
         }
         else {
             stockVO = SinaStockApi.assembleStockVO(SinaStockApi.getSinaStock(option.getStockGid()));
         }

         stockOptionListVO.setNowPrice(stockVO.getNowPrice());

         stockOptionListVO.setHcrate(stockVO.getHcrate().toString());

         stockOptionListVO.setPreclose_px(stockVO.getPreclose_px());

         stockOptionListVO.setOpen_px(stockVO.getOpen_px());

         Stock stock = this.stockMapper.selectByPrimaryKey(option.getStockId());

         stockOptionListVO.setStock_plate(stock.getStockPlate());

         if(option.getStockGid().contains("HK")){
             stockOptionListVO.setStock_type("HK");
         }else {
             stockOptionListVO.setStock_type(stock.getStockType());
         }
         return stockOptionListVO;

     }
 }
