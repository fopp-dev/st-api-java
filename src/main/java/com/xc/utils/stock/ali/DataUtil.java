package com.xc.utils.stock.ali;

import com.xc.utils.stock.ali.CacheItem;
import com.xc.utils.stock.ali.CacheManager;
import com.xc.utils.stock.ali.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataUtil {
    private static final String host = "http://alirm-com.konpn.com";
    /// <summary>
    /// 这里是阿里云接口授权appCode
    /// 更换成自己的
    /// </summary>
    private static String appcode = "80ccb856dbf044959ed0c09d72183664";

    //或者使用平台接口
    //private static final String host = "http://map.konpn.com:10002/localrm";
    //private static String appcode = "xxx";

    /// <summary>
    /// 获取实时行情列表
    /// 逗号分隔，最多10个
    /// </summary>
    public static String GetMaps(String symbols) throws Exception {

        String path = "/query/comrms";
        String method = "GET";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("symbols", symbols);

        /**
         * 重要提示如下:
         * HttpUtils请从
         * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
         * 下载
         *
         * 相应的依赖请参照
         * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
         */
        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        //return response.toString();
        return EntityUtils.toString(response.getEntity());
    }

    /// <summary>
    /// 获取实时资讯
    /// </summary>
    public static String GetQNews(int pidx, String types) throws Exception {
        String path = "/query/qnews";
        String method = "GET";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("pidx", Integer.toString(pidx));
        querys.put("types", types);
        querys.put("ps", "10");

        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        //return response.toString();
        return EntityUtils.toString(response.getEntity());
    }
    public static String GetcomMaps(String symbol) throws Exception
    {
        String path = "/query/com";
        String method = "GET";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("symbol", symbol);
        querys.put("withticks", "0");
        querys.put("withks", "0");

        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        //return response.toString();
        return EntityUtils.toString(response.getEntity());
    }
    public static String GetLstKMaps(String symbol, String period, long fromtick) throws Exception
    {
        if(period.equals("T")){period="1M";}

        String path = "/query/comlstkm";
        String method = "GET";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("fromtick", Long.toString(fromtick));
        querys.put("period", period);
        querys.put("symbol", symbol);
        querys.put("rout", "*");

        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        //return response.toString();
        return EntityUtils.toString(response.getEntity());
    }

    public static String GetLstKM4Maps(String symbol, String period, String date) throws Exception
    {
        if(period.equals("T")){period="1M";}

        String path = "/query/comkm4";
        String method = "GET";

        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("date", date);
        querys.put("period", period);
        querys.put("symbol", symbol);
        querys.put("rout", "*");

        HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
        //return response.toString();
        return EntityUtils.toString(response.getEntity());
    }
    /// <summary>
    /// 获取某品种某周期的一次性k线
    /// 3分钟缓存,最后修复的至少3个
    /// </summary>
    /// <param name="symbol">品种</param>
    /// <param name="period">周期, 1M,5M,15M,30M,1H,D</param>
    public static String GetKMaps(String symbol, String period,  Integer size) throws Exception {

        if(period.equals("T")){period="1M";}

        String ckey = getCacheKey(symbol, period);
        String kmsret = "";

        CacheItem ci = CacheManager.getCacheInfo(ckey);
        if (ci != null && !ci.isExpired())
            kmsret = ci.getValue().toString();

        if (kmsret.isEmpty()) {
            Map<String, String> headers = new HashMap<String, String>();
            //最后在heade中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
            headers.put("Authorization", "APPCODE " + appcode);
            Map<String, String> querys = new HashMap<String, String>();

            JSONArray jsonArray = new JSONArray();
            for (int i = 1; i < 2; i++) {//每页n，取1页即可
                //每次n条
                String path = String.format("/query/comkm?psize=%s&period=%s&pidx=%s&rout=*&symbol=%s&withlast=1",
                        size,
                        URLEncoder.encode(period,"UTF-8"),
                        i,
                        symbol);
                HttpResponse response = HttpUtils.doGet(host, path, "GET", headers, querys);
                //return response.toString();
                String reqretStr = EntityUtils.toString(response.getEntity());
                JSONObject obj = new JSONObject(reqretStr);
                if (obj.getInt("Code") >= 0) {
                    JSONArray tempobj = obj.getJSONArray("Obj");
                    if (tempobj.length() > 0) {
                        for (int j = 0; j < tempobj.length(); j++)
                            jsonArray.put(tempobj.get(j));
                    } else break;
                }
            }

            if (jsonArray.length() > 0) {
                JSONObject rets = new JSONObject();
                rets.put("Code", 0);
                rets.put("Obj", jsonArray);
                kmsret = rets.toString();

                //按周期缓存时间
                if (period.equals("D") || period.equals("W") || period.equals("M"))
                    CacheManager.putCache(ckey, new CacheItem(ckey, kmsret, (System.currentTimeMillis() + 10 * 60 * 1000), false));
                else if (period.equals("1M"))
                    CacheManager.putCache(ckey, new CacheItem(ckey, kmsret, (System.currentTimeMillis() + 1* 60 * 1000), false));
                else if (period.equals("5M"))
                    CacheManager.putCache(ckey, new CacheItem(ckey, kmsret, (System.currentTimeMillis() + 2 * 60 * 1000), false));
                else
                    CacheManager.putCache(ckey, new CacheItem(ckey, kmsret, (System.currentTimeMillis() + 5 * 60 * 1000), false));
            }
        }

        return kmsret;
    }
    /*
      public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
     12     public static final String MINUTE_PATTERN = "yyyy-MM-dd HH:mm";
     13     public static final String HOUR_PATTERN = "yyyy-MM-dd HH:mm:ss";
     14     public static final String DATE_PATTERN = "yyyy-MM-dd";
     15     public static final String MONTH_PATTERN = "yyyy-MM";
     16     public static final String YEAR_PATTERN = "yyyy";
     17     public static final String MINUTE_ONLY_PATTERN = "mm";
     18     public static final String HOUR_ONLY_PATTERN = "HH";
    * */
    static String getCacheKey(String symbol, String period)
    {
        Date dt=new Date();

        String datekey = new SimpleDateFormat("MMddHHmm").format(dt);
        if (period.equals("D") || period.equals("W") || period.equals("M")) { datekey = new SimpleDateFormat("MMdd").format(dt);}
        else if (period.equals("1M")) { datekey = new SimpleDateFormat("MMddHHmm").format(dt); }
        else if (period.equals("5M")) datekey = new SimpleDateFormat("MMddHH").format(dt) + String.valueOf((int)(dt.getMinutes() / 5));
        else if (period.equals("15M")) datekey = new SimpleDateFormat("MMddHH").format(dt) + String.valueOf((int)(dt.getMinutes() / 15));
        else if (period.equals("30M")) datekey = new SimpleDateFormat("MMddHH").format(dt) + String.valueOf((int)(dt.getMinutes() / 30));
        else if (period.equals("1H")) datekey =new SimpleDateFormat("MMddHH").format(dt);
        else if (period.equals("4H")) datekey = new SimpleDateFormat("MMdd").format(dt) + String.valueOf((int)(dt.getHours() / 4));

        return String.format("queryechart%s%s%s", symbol, period, datekey);
    }
}
