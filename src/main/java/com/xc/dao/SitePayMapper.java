package com.xc.dao;

import com.xc.pojo.SitePay;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SitePayMapper {
  int deleteByPrimaryKey(Integer paramInteger);
  
  int insert(SitePay paramSitePay);
  
  int insertSelective(SitePay paramSitePay);
  
  SitePay selectByPrimaryKey(Integer paramInteger);
  
  int updateByPrimaryKeySelective(SitePay paramSitePay);
  
  int updateByPrimaryKey(SitePay paramSitePay);
  
  SitePay findByChannelType(@Param("channelType") String paramString);
  
  List<SitePay> listByAdmin(@Param("channelType") String paramString);
  
  List getPayInfo(@Param("payAmt") BigDecimal payAmt);
}
