package com.xc.service;

import com.github.pagehelper.PageInfo;
import com.xc.common.ServerResponse;
import com.xc.pojo.StockGg;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface IStockGgService {

    ServerResponse getSingleStock(String paramString);

    ServerResponse getStockGg(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, HttpServletRequest request);

    ServerResponse<StockGg> findStockGgByName(String paramString);

    ServerResponse<StockGg> findStockGgByCode(String paramString);

    ServerResponse<StockGg> findStockGgById(Integer paramInteger);

    ServerResponse<PageInfo> listByAdmin(Integer paramInteger1, Integer paramInteger2, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, HttpServletRequest paramHttpServletRequest);

    ServerResponse updateLock(Integer paramInteger);

    ServerResponse updateShow(Integer paramInteger);

    ServerResponse addStockGg(String paramString1, String paramString2, String paramString3, String paramString4, Integer paramInteger1, Integer paramInteger2);

    int CountStockGgNum();

    int CountShowNum(Integer paramInteger);

    int CountUnLockNum(Integer paramInteger);

    List findStockGgList();

    ServerResponse selectRateByDaysAndStockGgCode(String paramString, int paramInt);

    ServerResponse updateStockGg(StockGg model);
}
