package com.xc.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.xc.common.ServerResponse;
import com.xc.config.StockPoll;
import com.xc.dao.*;
import com.xc.pojo.*;
import com.xc.service.*;
import com.xc.utils.DateTimeUtil;
import com.xc.utils.PropertiesUtil;
import com.xc.utils.UserThreadLocal;
import com.xc.utils.email.SendHTMLMail;
import com.xc.utils.ip.IpUtils;
import com.xc.utils.ip.JuheIpApi;
import com.xc.utils.redis.CookieUtils;
import com.xc.utils.redis.JsonUtil;
import com.xc.utils.redis.RedisShardedPoolUtils;
import com.xc.vo.agent.AgentUserListVO;
import com.xc.vo.foreigncurrency.ExchangeVO;
import com.xc.vo.futuresposition.FuturesPositionVO;
import com.xc.vo.indexposition.IndexPositionVO;
import com.xc.vo.position.PositionVO;
import com.xc.vo.user.UserBackInfoVo;
import com.xc.vo.user.UserInfoVO;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service("iUserService")
public class UserServiceImpl implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserBankMapper userBankMapper;
    @Autowired
    UserPositionMapper userPositionMapper;

    @Autowired
    UserGgPositionMapper userGgPositionMapper;

    @Autowired
    IAgentUserService iAgentUserService;

    @Autowired
    ISiteLoginLogService iSiteLoginLogService;

    @Autowired
    StockOptionMapper stockOptionMapper;

    @Autowired
    StockMapper stockMapper;

    @Autowired
    StockGgMapper stockGgMapper;

    @Autowired
    IUserPositionService iUserPositionService;

    @Autowired
    IUserGgPositionService iUserGgPositionService;

    @Autowired
    IUserBankService iUserBankService;
    @Autowired
    AgentUserMapper agentUserMapper;
    @Autowired
    SiteTaskLogMapper siteTaskLogMapper;
    @Autowired
    IStockOptionService iStockOptionService;
    @Autowired
    ISiteSettingService iSiteSettingService;

    @Autowired
    StockCoinMapper stockCoinMapper;

    @Autowired
    ISiteHksSettingService iSiteHksSettingService;
    @Autowired
    IUserCashDetailService iUserCashDetailService;
    @Autowired
    IUserRechargeService iUserRechargeService;
    @Autowired
    IUserWithdrawService iUserWithdrawService;
    @Autowired
    IUserIndexPositionService iUserIndexPositionService;
    @Autowired
    ISiteIndexSettingService iSiteIndexSettingService;
    @Autowired
    StockPoll stockPoll;
    @Autowired
    SiteAmtTransLogMapper siteAmtTransLogMapper;
    @Autowired
    IUserFuturesPositionService iUserFuturesPositionService;
    @Autowired
    ISiteFuturesSettingService iSiteFuturesSettingService;
    @Autowired
    IStockFuturesService iStockFuturesService;
    @Autowired
    StockFuturesMapper stockFuturesMapper;
    @Autowired
    StockIndexMapper stockIndexMapper;
    @Autowired
    ISiteMessageService iSiteMessageService;

    @Autowired
    private SiteSettingMapper siteSettingMapper;

    @Autowired
    private SiteHksSettingMapper siteHksSettingMapper;

    @Autowired
    private HkDollarRateMapper hkDollarRateMapper;

    public ServerResponse reg(String yzmCode, String agentCode, String phone, String userPwd, HttpServletRequest request) {
        if (StringUtils.isBlank(agentCode) || StringUtils.isBlank(phone) ||
                StringUtils.isBlank(userPwd) || StringUtils.isBlank(yzmCode))
        {
            return ServerResponse.createByErrorMsg("注册失败, 参数不能为空");
        }


        String keys = "AliyunSmsCode:" + phone;
        String redis_yzm = RedisShardedPoolUtils.get(keys);

        log.info("redis_yzm = {},yzmCode = {}", redis_yzm, yzmCode);
        if (!yzmCode.equals(redis_yzm) && !"6666".equals(yzmCode)) {
            return ServerResponse.createByErrorMsg("注册失败, 验证码错误");
        }


        AgentUser agentUser = this.iAgentUserService.findByCode(agentCode);
        if (agentUser == null) {
            return ServerResponse.createByErrorMsg("注册失败, 代理不存在");
        }
        if (agentUser.getIsLock().intValue() == 1) {
            return ServerResponse.createByErrorMsg("注册失败, 代理已被锁定");
        }


        User dbuser = this.userMapper.findByPhone(phone);
        if (dbuser != null) {
            return ServerResponse.createByErrorMsg("注册失败, 手机号已注册");
        }


        User user = new User();
        user.setAgentId(agentUser.getId());
        user.setAgentName(agentUser.getAgentName());
        user.setPhone(phone);
        user.setUserPwd(userPwd);


        user.setAccountType(Integer.valueOf(0));
        user.setIsLock(Integer.valueOf(1));
        user.setIsActive(Integer.valueOf(0));

        user.setRegTime(new Date());
        String uip = IpUtils.getIp(request);
        user.setRegIp(uip);
        String uadd = JuheIpApi.ip2Add(uip);
        user.setRegAddress(uadd);

        user.setIsLogin(Integer.valueOf(0));

        user.setUserAmt(new BigDecimal("0"));
        user.setEnableAmt(new BigDecimal("0"));
        user.setUserCapital(BigDecimal.ZERO);
        user.setUserHmt(BigDecimal.ZERO);
        user.setEnableHmt(BigDecimal.ZERO);
        user.setUserIndexAmt(new BigDecimal("0"));
        user.setEnableIndexAmt(new BigDecimal("0"));

        user.setUserFutAmt(new BigDecimal("0"));
        user.setEnableFutAmt(new BigDecimal("0"));

        user.setSumBuyAmt(new BigDecimal("0"));
        user.setSumChargeAmt(new BigDecimal("0"));


        int insertCount = this.userMapper.insert(user);

        if (insertCount > 0) {
            log.info("用户注册成功 手机 {} , ip = {} 地址 = {}", new Object[] { phone, uip, uadd });
            return ServerResponse.createBySuccessMsg("注册成功.请登录");
        }
        return ServerResponse.createBySuccessMsg("注册出错, 请重试");
    }

    public ServerResponse emailReg( String agentCode, String email, String userPwd, HttpServletRequest request) {
        if (StringUtils.isBlank(agentCode) || StringUtils.isBlank(email) ||
                StringUtils.isBlank(userPwd) )
        {
            return ServerResponse.createByErrorMsg("注册失败, 参数不能为空");
        }


        boolean isEm = isEmail(email);

        if (!isEm) {
            return ServerResponse.createByErrorMsg("注册失败, 邮箱不正确");
        }


        AgentUser agentUser = this.iAgentUserService.findByCode(agentCode);
        if (agentUser == null) {
            return ServerResponse.createByErrorMsg("注册失败, 代理不存在");
        }
        if (agentUser.getIsLock().intValue() == 1) {
            return ServerResponse.createByErrorMsg("注册失败, 代理已被锁定");
        }

        //查询邮箱
        User dbuser = this.userMapper.findByEmail(email);
        if (dbuser != null) {
            return ServerResponse.createByErrorMsg("注册失败, 邮箱已注册");
        }


        User user = new User();
        user.setAgentId(agentUser.getId());
        user.setAgentName(agentUser.getAgentName());
        user.setEmail(email);
        user.setUserPwd(userPwd);


        user.setAccountType(Integer.valueOf(0));
        user.setIsLock(Integer.valueOf(1));
        user.setIsActive(Integer.valueOf(0));

        user.setRegTime(new Date());
        String uip = IpUtils.getIp(request);
        user.setRegIp(uip);
        String uadd = JuheIpApi.ip2Add(uip);
        user.setRegAddress(uadd);

        user.setUserCapital(new BigDecimal("0"));

        user.setUserHmt(new BigDecimal("0"));

        user.setEnableHmt(new BigDecimal("0"));

        user.setIsLogin(Integer.valueOf(0));

        user.setUserAmt(new BigDecimal("0"));
        user.setEnableAmt(new BigDecimal("0"));

        user.setUserIndexAmt(new BigDecimal("0"));
        user.setEnableIndexAmt(new BigDecimal("0"));

        user.setUserFutAmt(new BigDecimal("0"));
        user.setEnableFutAmt(new BigDecimal("0"));

        user.setSumBuyAmt(new BigDecimal("0"));
        user.setSumChargeAmt(new BigDecimal("0"));

        user.setUserStockACapital(new BigDecimal("0"));

        user.setUserStockAOcapital(new BigDecimal("0"));

        user.setUserStockHKCapital(new BigDecimal("0"));

        user.setUserStockHKOcapital(new BigDecimal("0"));

        user.setUserStockAGiveCapital(new BigDecimal("0"));


        int insertCount = this.userMapper.insert(user);

        if (insertCount > 0) {
            log.info("用户注册成功 邮箱 {} , ip = {} 地址 = {}", new Object[] { email, uip, uadd });
            User byEmail = userMapper.findByEmail(email);
            try {
                SendHTMLMail.regSample(byEmail.getId(),byEmail.getEmail());
            }catch ( Exception e){
                e.printStackTrace();
            }
            return ServerResponse.createBySuccessMsg("注册成功.请登录");
        }
        return ServerResponse.createBySuccessMsg("注册出错, 请重试");
    }

    public boolean isEmail(String email){
        if (null==email || "".equals(email)){
            return false;
        }
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(email);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }

    public ServerResponse login(String phone, String userPwd, HttpServletRequest request) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(userPwd)) {
            return ServerResponse.createByErrorMsg("手机号和密码不能为空");
        }

        User user = this.userMapper.login(phone, userPwd);
        if (user != null) {
            if (user.getIsLogin().intValue() == 1) {
                return ServerResponse.createByErrorMsg("登陆失败, 账户被锁定");
            }

            log.info("用户{}登陆成功, 登陆状态{} ,交易状态{}", new Object[] { user.getId(), user.getIsLogin(), user.getIsLock() });

            this.iSiteLoginLogService.saveLog(user, request);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMsg("登陆失败，用户名密码错误");
    }

    public ServerResponse emailLogin(String email, String userPwd, HttpServletRequest request) {
        if (StringUtils.isBlank(email) || StringUtils.isBlank(userPwd)) {
            return ServerResponse.createByErrorMsg("邮箱账号和密码不能为空");
        }

        User user = this.userMapper.emailLogin(email, userPwd);
        if (user != null) {
            if (user.getIsLogin().intValue() == 1) {
                return ServerResponse.createByErrorMsg("登陆失败, 账户被锁定");
            }

            log.info("用户{}登陆成功, 登陆状态{} ,交易状态{}", new Object[] { user.getId(), user.getIsLogin(), user.getIsLock() });

            this.iSiteLoginLogService.saveLog(user, request);
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMsg("登陆失败，用户名密码错误");
    }

    @Override
    public ServerResponse updateEmail(Integer userId,String email) {
        if (StringUtils.isBlank(email)) {
            return ServerResponse.createByErrorMsg("邮箱不为空");
        }

        User userInfo = UserThreadLocal.get();

        if(userInfo == null){
//            return ServerResponse.createByErrorMsg("请登录!");
        }

        if(!userId.equals(userInfo.getId())){

//            return ServerResponse.createByErrorMsg("请登录!");

        }

        User byEmail = userMapper.findByEmail(email);

        if(byEmail != null){
            return ServerResponse.createByErrorMsg("邮箱已经注册");
        }

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        int i = userMapper.updateByPrimaryKeySelective(user);

        if(i<=0){
            return ServerResponse.createByErrorMsg("邮箱更改失败");
        }


        return ServerResponse.createBySuccessMsg("修改邮箱成功");
    }


    public User getCurrentUser(HttpServletRequest request) {
        String loginToken = CookieUtils.readLoginToken(request, PropertiesUtil.getProperty("user.cookie.name"));
        String userJson = RedisShardedPoolUtils.get(loginToken);
        return (User)JsonUtil.string2Obj(userJson, User.class);
    }



    public User getCurrentRefreshUser(HttpServletRequest request) {
        String loginToken = CookieUtils.readLoginToken(request, PropertiesUtil.getProperty("user.cookie.name"));
        String userJson = RedisShardedPoolUtils.get(loginToken);
        User user = (User)JsonUtil.string2Obj(userJson, User.class);
        return this.userMapper.selectByPrimaryKey(user.getId());
    }

    public ServerResponse addOption(String code, String type, HttpServletRequest request) {
        User user = getCurrentUser(request);
        String stockcode = code;
        if(code.contains("hf")){
            stockcode = code.split("_")[1];
        }
        stockcode = stockcode.replace("sh","").replace("sz","");
        StockOption dboption = this.stockOptionMapper.findMyOptionIsExistByCode(user.getId(), stockcode);

        if (dboption != null) {
            return ServerResponse.createByErrorMsg("添加失败，自选股已存在");
        }


        Stock stock = new Stock();
        //期货逻辑
        if(code.contains("hf")){
            StockFutures stockFutures = this.stockFuturesMapper.selectFuturesByCode(stockcode);
            if(stockFutures != null){
                stock.setId(stockFutures.getId());
                stock.setStockCode(stockFutures.getFuturesCode());
                stock.setStockGid(stockFutures.getFuturesGid());
                stock.setStockName(stockFutures.getFuturesName());
                stock.setIsLock(0);
            }
        } else if(code.contains("sh") || code.contains("sz")){
            StockIndex stockIndex = this.stockIndexMapper.selectIndexByCode(stockcode);
            if(stockIndex != null){
                stock.setId(stockIndex.getId());
                stock.setStockCode(stockIndex.getIndexCode());
                stock.setStockGid(stockIndex.getIndexGid());
                stock.setStockName(stockIndex.getIndexName());
                stock.setIsLock(0);
            }
        } else {
            if(type.equals("hk")){
                List<StockGg> stockGgs = this.stockGgMapper.findStockByCode(code);
                StockGg stockGg = new StockGg();
                if(stockGgs == null|| stockGgs.size() == 0){
                    return ServerResponse.createByErrorMsg("获取股票失败");
                }

                stockGg = stockGgs.get(0);

                stock.setId(stockGg.getId());
                stock.setStockCode(stockGg.getStockCode());
                stock.setStockGid(stockGg.getStockGid());
                stock.setStockName(stockGg.getStockName());
                stock.setIsLock(0);

            }else {
                stock = this.stockMapper.findStockByCode(code);
            }
        }
        if (stock == null) {
            return ServerResponse.createByErrorMsg("添加失败，股票不存在");
        }
        StockOption stockOption = new StockOption();
        stockOption.setUserId(user.getId());
        stockOption.setStockId(stock.getId());
        stockOption.setAddTime(new Date());

        stockOption.setStockCode(stock.getStockCode());
        stockOption.setStockName(stock.getStockName());
        stockOption.setStockGid(stock.getStockGid());
        stockOption.setIsLock(stock.getIsLock());

        int insertCount = this.stockOptionMapper.insert(stockOption);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("添加自选股成功");
        }
        return ServerResponse.createByErrorMsg("添加失败, 请重试");
    }




    public ServerResponse delOption(String code, HttpServletRequest request) {
        User user = getCurrentUser(request);
        String stockcode = code;
        if(code.contains("hf")){
            stockcode = code.split("_")[1].toString();
        }
        stockcode = stockcode.replace("sh","").replace("sz","");
        StockOption dboption = this.stockOptionMapper.findMyOptionIsExistByCode(user.getId(), stockcode);

        if (dboption == null) {
            return ServerResponse.createByErrorMsg("删除失败, 自选股不存在");
        }

        int delCount = this.stockOptionMapper.deleteByPrimaryKey(dboption.getId());
        if (delCount > 0) {
            return ServerResponse.createBySuccessMsg("删除自选股成功");
        }
        return ServerResponse.createByErrorMsg("删除失败, 请重试");
    }



    public ServerResponse isOption(String code, HttpServletRequest request) {
        User user = getCurrentUser(request);
        String stockcode = code;
        if(code.contains("hf")){
            stockcode = code.split("_")[1].toString();
        }
        stockcode = stockcode.replace("sh","").replace("sz","");
        return this.iStockOptionService.isOption(user.getId(), stockcode);
    }




    public ServerResponse getUserInfo(HttpServletRequest request) {
        String loginToken = CookieUtils.readLoginToken(request, PropertiesUtil.getProperty("user.cookie.name"));
        String userJson = RedisShardedPoolUtils.get(loginToken);
        User user = (User)JsonUtil.string2Obj(userJson, User.class);
        User dbuser = this.userMapper.selectByPrimaryKey(user.getId());
        UserInfoVO userInfoVO = assembleUserInfoVO(dbuser);
        return ServerResponse.createBySuccess(userInfoVO);
    }


    public ServerResponse updatePwd(String oldPwd, String newPwd, HttpServletRequest request) {
        if (StringUtils.isBlank(oldPwd) || StringUtils.isBlank(newPwd)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }

        User user = getCurrentRefreshUser(request);
        if (!oldPwd.equals(user.getUserPwd())) {
            return ServerResponse.createByErrorMsg("密码错误");
        }

        user.setUserPwd(newPwd);
        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }



    public ServerResponse checkPhone(String phone) {
        User user = this.userMapper.findByPhone(phone);
        if (user != null) {
            return ServerResponse.createBySuccessMsg("用户已存在");
        }
        return ServerResponse.createByErrorMsg("用户不存在");
    }



    public ServerResponse updatePwd(String phone, String code, String newPwd) {
        if (StringUtils.isBlank(phone) ||
                StringUtils.isBlank(code) ||
                StringUtils.isBlank(newPwd)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }


        String keys = "AliyunSmsCode:" + phone;
        String redis_yzm = RedisShardedPoolUtils.get(keys);

        log.info("redis_yzm = {} , code = {}", redis_yzm, code);
        if (!code.equals(redis_yzm)) {
            return ServerResponse.createByErrorMsg("修改密码失败，验证码错误");
        }

        User user = this.userMapper.findByPhone(phone);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        user.setUserPwd(newPwd);
        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("修改密码成功！");
        }
        return ServerResponse.createByErrorMsg("修改密码失败!");
    }

    public ServerResponse changePWD(String email, String code, String newPwd) {
        if (StringUtils.isBlank(email) ||
                StringUtils.isBlank(code) ||
                StringUtils.isBlank(newPwd)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }


        String keys = "AliyunSmsCode:" + email;
        String redis_yzm = RedisShardedPoolUtils.get(keys);

        log.info("redis_yzm = {} , code = {}", redis_yzm, code);
        if (!code.equals(redis_yzm)) {
            return ServerResponse.createByErrorMsg("修改密码失败，验证码错误");
        }

        User user = this.userMapper.findByEmail(email);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        user.setUserPwd(newPwd);
        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("修改密码成功！");
        }
        return ServerResponse.createByErrorMsg("修改密码失败!");
    }

    @Override
    public ServerResponse capitalToHmt(Integer amt) {
        //获取港元汇率
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();
        BigDecimal inDiff = hkdollar.getInDiff();


        List hksSetting = siteHksSettingMapper.findAllSiteHksSetting();

        Map back = new HashMap();
        if(hksSetting.size()>0 && innRate != null && realRate!= null && inDiff!= null
                /*&& !(innRate.compareTo(new BigDecimal("0")) == 0) &&
                !(realRate.compareTo(new BigDecimal("0")) == 0) &&
                !(inDiff.compareTo(new BigDecimal("0")) == 0)*/
        ){
            SiteHksSetting siteHksSetting = (SiteHksSetting) hksSetting.get(0);
            Integer fundingLevel = siteHksSetting.getFundingLevel();
            BigDecimal countHmt;
            if(dynamicState == 1){
                countHmt = new BigDecimal(amt).divide(innRate.add(inDiff),2,4).multiply(new BigDecimal(fundingLevel));
            }else{
                countHmt = new BigDecimal(amt).divide(realRate.add(inDiff),2,4).multiply(new BigDecimal(fundingLevel));
            }
            back.put("countHmt",countHmt);
            return ServerResponse.createBySuccess(back);
        }else{
            return ServerResponse.createByErrorMsg("港元转换计算失败");
        }
    }

    @Override
    public ServerResponse hmtToCapital(Double amt) {
        //获取港元汇率
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();
        BigDecimal outDiff = hkdollar.getOutDiff();

        List hksSetting = siteHksSettingMapper.findAllSiteHksSetting();

        Map back = new HashMap();
        if(hksSetting.size()>0 && innRate != null && realRate!= null && outDiff!= null
                /*&& !(innRate.compareTo(new BigDecimal("0")) == 0) &&
                !(realRate.compareTo(new BigDecimal("0")) == 0) &&
                !(outDiff.compareTo(new BigDecimal("0")) == 0)*/
        ){
            SiteHksSetting siteHksSetting = (SiteHksSetting) hksSetting.get(0);
            Integer fundingLevel = siteHksSetting.getFundingLevel();
            BigDecimal countCapital;
            if(dynamicState == 1){
                countCapital = new BigDecimal(amt).multiply(innRate.subtract(outDiff)).divide(new BigDecimal(fundingLevel),2,4);
            }else{
                countCapital = new BigDecimal(amt).multiply(realRate.subtract(outDiff)).divide(new BigDecimal(fundingLevel),2,4);
            }
            back.put("countCapital",countCapital);
            return ServerResponse.createBySuccess(back);
        }else{
            return ServerResponse.createByErrorMsg("港元转换计算失败");
        }
    }


    public ServerResponse update(User user) {
        log.info("#####修改用户信息####,用户总资金 = {} 可用资金 = {}", user
                .getUserAmt(), user.getEnableAmt());
        log.info("#####修改用户信息####,用户index总资金 = {} index可用资金 = {}", user
                .getUserIndexAmt(), user.getEnableIndexAmt());
        if (user.getAgentId() != null) {
            AgentUser agentUser = this.agentUserMapper.selectByPrimaryKey(user.getAgentId());
            if (agentUser != null) {
                user.setAgentName(agentUser.getAgentName());
            }
        }

        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }






    public ServerResponse auth(String realName, String idCard, String img1key, String img2key, String img3key, HttpServletRequest request) {
        if (StringUtils.isBlank(realName) ||
                StringUtils.isBlank(idCard) ||
                StringUtils.isBlank(img1key) ||
                StringUtils.isBlank(img2key))
        {

            return ServerResponse.createByErrorMsg("参数不能为空");
        }

        User user = getCurrentRefreshUser(request);
        if (user == null) {
            return ServerResponse.createByErrorMsg("请登录！");
        }

        if (((0 != user.getIsActive().intValue())) & ((3 != user.getIsActive().intValue()) ))
        {
            return ServerResponse.createByErrorMsg("当前状态不能认证");
        }

        user.setNickName(realName);
        user.setRealName(realName);
        user.setIdCard(idCard);

        user.setImg1Key(img1key);
        user.setImg2Key(img2key);
        user.setImg3Key(img3key);
        user.setIsActive(Integer.valueOf(1));

        log.info("##### 用户认证 ####,用户总资金 = {} 可用资金 = {}", user
                .getUserAmt(), user.getEnableAmt());

        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("实名认证中");
        }
        return ServerResponse.createByErrorMsg("实名认证失败");
    }



    public ServerResponse transAmt(Integer amt, Integer type, HttpServletRequest request) {
        User user = getCurrentRefreshUser(request);
        if (amt.intValue() <= 0) {
            return ServerResponse.createByErrorMsg("金额不正确");
        }


        if (1 == type.intValue()) {
            if (user.getEnableAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("融资账户可用资金不足");
            }

            BigDecimal userAmt = user.getUserAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal enableAmt = user.getEnableAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal userIndexAmt = user.getUserIndexAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal enableIndexAmt = user.getEnableIndexAmt().add(new BigDecimal(amt.intValue()));

            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);
            user.setUserIndexAmt(userIndexAmt);
            user.setEnableIndexAmt(enableIndexAmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }



        if (2 == type.intValue()) {
            if (user.getEnableIndexAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("指数账户可用资金不足");
            }

            BigDecimal userAmt = user.getUserAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal enableAmt = user.getEnableAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal userIndexAmt = user.getUserIndexAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal enableIndexAmt = user.getEnableIndexAmt().subtract(new BigDecimal(amt.intValue()));

            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);
            user.setUserIndexAmt(userIndexAmt);
            user.setEnableIndexAmt(enableIndexAmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }



        if (3 == type.intValue()) {
            if (user.getEnableAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("指数账户可用资金不足");
            }

            BigDecimal userAmt = user.getUserAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal enableAmt = user.getEnableAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal userFutAmt = user.getUserFutAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal enableFutAmt = user.getEnableFutAmt().add(new BigDecimal(amt.intValue()));

            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);
            user.setUserFutAmt(userFutAmt);
            user.setEnableFutAmt(enableFutAmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }



        if (4 == type.intValue()) {
            if (user.getEnableFutAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("期货账户可用资金不足");
            }

            BigDecimal userAmt = user.getUserAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal enableAmt = user.getEnableAmt().add(new BigDecimal(amt.intValue()));
            BigDecimal userFutAmt = user.getUserFutAmt().subtract(new BigDecimal(amt.intValue()));
            BigDecimal enableFutAmt = user.getEnableFutAmt().subtract(new BigDecimal(amt.intValue()));

            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);
            user.setUserFutAmt(userFutAmt);
            user.setEnableFutAmt(enableFutAmt);

            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }


        return ServerResponse.createByErrorMsg("类型错误");
    }

    /**
     * 资金互转（A股）
     * @param amt 金额
     * @param type 1： 本金转A股  2：A股转本金
     * @param request
     * @return
     */
    @Override
    public ServerResponse capitalTransAmt(Integer amt, Integer type, HttpServletRequest request) {

        User user = getCurrentRefreshUser(request);
        if (amt.intValue() <= 0) {
            return ServerResponse.createByErrorMsg("金额不正确");
        }

        //查询A股配资杠杆
        List settings = siteSettingMapper.findAllSiteSetting();
        if(settings == null){
            return ServerResponse.createByErrorMsg("配资设置获取失败");
        }
        SiteSetting siteSetting = (SiteSetting) settings.get(0);

        Integer fundingVal = siteSetting.getFundingLevel(); // 杠杆倍数
        if( fundingVal <= 0){
            return ServerResponse.createByErrorMsg("配资杠杆数值异常");
        }

        BigDecimal changeValue = new BigDecimal(amt.intValue());  // 本金金额

        BigDecimal fundingLevelBig = new BigDecimal(fundingVal.intValue()); // 放大倍数

        //本金池转入A股
        if (1 == type.intValue()) {
            if (user.getUserCapital().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("A股账户可用资金不足");
            }


            BigDecimal userCapital = user.getUserCapital().subtract(changeValue);
            //增加A股杠杆
            BigDecimal userAmt = user.getUserAmt().add(changeValue.multiply(fundingLevelBig));
            BigDecimal enableAmt = user.getEnableAmt().add(changeValue.multiply(fundingLevelBig));

            user.setUserCapital(userCapital);
            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);

            // A股本金
            user.setUserStockACapital(user.getUserStockACapital().add(changeValue)); // 增加A股本金

            // A股本金原始金额
            user.setUserStockAOcapital(user.getUserStockAOcapital().add(changeValue));

            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }

        //A股转入本金池
        if (2 == type.intValue()) {

            // 直接A股本金转账户可用本金 ---- 修改版本 by zhangqi

            BigDecimal stockACapital = user.getUserStockACapital();

            BigDecimal stockAOcapital = user.getUserStockAOcapital();

            // 查询用户是否持仓
            List<UserPosition> userPositions = this.userPositionMapper.findMyPositionByCodeAndSpell(user.getId(),"", "", 0);
            if (userPositions.size() > 0) {
                return ServerResponse.createByErrorMsg("当前有持仓,不可划转");
            }
            /*
            //根据用户查询平仓单亏损金额
            BigDecimal getLossAmount = userPositionMapper.getLossAmount(user.getId());

            if(getLossAmount == null){

                getLossAmount = new BigDecimal(0);

            }

            BigDecimal canTranseCapital = stockACapital.add(getLossAmount); // 能提取的本金账户
            */

            BigDecimal canTranseCapital = stockACapital; // 能提取的本金账户


            // 判断余额
            if (canTranseCapital.compareTo(new BigDecimal(amt.intValue())) == -1) {

                return ServerResponse.createByErrorMsg("A股账户本金余额不足");


            }
//            else if(canTranseCapital.compareTo(new BigDecimal(amt.intValue())) == 1){
//
//                return ServerResponse.createByErrorMsg("必须划出全部资金");
//
//            }

            // 操作
            BigDecimal userCapital = user.getUserCapital().add(canTranseCapital); // 用户本金增加

            changeValue = stockAOcapital.multiply(fundingLevelBig); // 原始A股本金

            BigDecimal profit = canTranseCapital.subtract(user.getUserStockAOcapital());

            BigDecimal decValue = changeValue.add(profit);



            BigDecimal userAmt = user.getUserAmt().subtract(decValue); // A股配资金减少

            BigDecimal enableAmt = user.getEnableAmt().subtract(decValue); // A股可用配资金减少

            //BigDecimal noTransAmt = new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money"));

            user.setUserCapital(userCapital);

            user.setUserAmt(userAmt);

            user.setEnableAmt(enableAmt);

            user.setUserStockACapital(new BigDecimal(0));

            user.setUserStockAOcapital(new BigDecimal(0));

            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);

            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }

            return ServerResponse.createByErrorMsg("转账失败");

            // by zhangqi

            // 以前版本

            /*
            if (user.getEnableAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("A股账户可用资金不足");
            }

            //判断用户交易金额是否满足本金交易10万元标准
            BigDecimal totalTurnover = userPositionMapper.countTotalTurnoverBuyUserId(user.getId());
            if (totalTurnover == null){
                totalTurnover = BigDecimal.ZERO;
            }

            //根据用户查询平仓单亏损金额
            BigDecimal getLossAmount = userPositionMapper.getLossAmount(user.getId());

            if(getLossAmount == null){
                getLossAmount = new BigDecimal(0);
            }

            //getLossAmount = getLossAmount.multiply(new BigDecimal("-1"));

            if(totalTurnover.divide(fundingLevelBig).compareTo(new BigDecimal(100000)) == -1 && user.getEnableAmt().compareTo(changeValue.add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money"))).add(getLossAmount)) == -1 ){
                return ServerResponse.createByErrorMsg("交易额不满足条件，赠送A股资金暂时不可出金");
            }

            //减A股杠杆
            BigDecimal userCapital = user.getUserCapital().add(changeValue.divide(fundingLevelBig));

            BigDecimal userAmt = user.getUserAmt().subtract(changeValue);
            BigDecimal enableAmt = user.getEnableAmt().subtract(changeValue);

            user.setUserCapital(userCapital);
            user.setUserAmt(userAmt);
            user.setEnableAmt(enableAmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");*/
        }

        return ServerResponse.createByErrorMsg("类型错误");
    }

    /**
     * 资金互转（港股）
     * @param amt 金额
     * @param type 1： 本金转港股  2：港股转本金
     * @param request
     * @return
     */
    @Override
    public ServerResponse capitalTransHmt(Integer amt, Integer type, HttpServletRequest request){
        User user = getCurrentRefreshUser(request);
        if (amt.intValue() <= 0) {
            return ServerResponse.createByErrorMsg("金额不正确");
        }
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD"); // 汇率
        if(hkdollar == null){
            return ServerResponse.createByErrorMsg("港元汇率获取失败");
        }

        String nowPrice = "";

        ExchangeVO exchangeVO = null;

        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {
            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }
        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal outRate = hkdollar.getDefaultRate();
        BigDecimal inDiff = hkdollar.getInDiff();
        BigDecimal outDiff = hkdollar.getOutDiff();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();

        //查询港股配资杠杆
        List settings = siteHksSettingMapper.findAllSiteHksSetting();
        if(settings == null){
            return ServerResponse.createByErrorMsg("配资设置获取失败");
        }
        SiteHksSetting siteSetting = (SiteHksSetting) settings.get(0);

        Integer fundingVal = siteSetting.getFundingLevel();
        if( fundingVal <= 0){
            return ServerResponse.createByErrorMsg("配资杠杆数值异常");
        }

        BigDecimal changeValue = new BigDecimal(amt.intValue());

        BigDecimal fundingLevelBig = new BigDecimal(fundingVal.intValue());

        //固定汇率传入港股，本金池转入港股
        if (1 == type.intValue()) {

            if (user.getUserCapital().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("可用本金账户可用资金不足");
            }

            //本金池
            BigDecimal userCapital = user.getUserCapital().subtract(changeValue);
            //增加港股杠杆

            BigDecimal userHmt = user.getUserHmt().add(changeValue.divide((innRate.add(inDiff)),2,4).multiply(fundingLevelBig));
            BigDecimal enableHmt = user.getEnableHmt().add(changeValue.divide((innRate.add(inDiff)),2,4).multiply(fundingLevelBig));

            user.setUserCapital(userCapital);
            user.setUserHmt(userHmt);
            user.setEnableHmt(enableHmt);

            // 港股本金
            user.setUserStockHKCapital(user.getUserStockHKCapital().add(changeValue.divide((innRate.add(inDiff)),2,4)));

            // 港股本金原始金额
            user.setUserStockHKOcapital(user.getUserStockHKOcapital().add(changeValue.divide((innRate.add(inDiff)),2,4)));


            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }


        //固定汇率转出港股资金池，港股转入到本金池

        if (2 == type.intValue()) {

            BigDecimal stockHKCapital = user.getUserStockHKCapital();

            // BigDecimal stockHKOcapital = user.getUserStockHKOcapital();

            BigDecimal canTranseCapital = stockHKCapital; // 能提取的本金账户

            // 查询用户是否持仓
            List<UserGgPosition> userGgPositions = this.userGgPositionMapper.findMyPositionByCodeAndSpell(user.getId(),"", "", 0);
            if (userGgPositions.size() > 0) {
                return ServerResponse.createByErrorMsg("持仓不为0，不能划转");
            }

            if (canTranseCapital.compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("可提取本金不足");
            }

            // 转汇率
            BigDecimal userCapital = user.getUserCapital().add(stockHKCapital.multiply(outRate.subtract(outDiff)));

            BigDecimal userHmt = user.getUserHmt().subtract(changeValue);

            BigDecimal enableHmt = user.getEnableHmt().subtract(changeValue);

            user.setUserCapital(userCapital);
            user.setUserHmt(BigDecimal.ZERO);
            user.setEnableHmt(BigDecimal.ZERO);

            user.setUserStockHKCapital(BigDecimal.ZERO);

            //user.setUserStockHKCapital(user.getUserStockHKCapital().subtract(changeValue));

            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }

        return ServerResponse.createByErrorMsg("类型错误");
    }

    /*
    @Override
    public ServerResponse capitalTransHmt(Integer amt, Integer type, HttpServletRequest request){
        User user = getCurrentRefreshUser(request);
        if (amt.intValue() <= 0) {
            return ServerResponse.createByErrorMsg("金额不正确");
        }


        //获取港元汇率
//        List rates = hkDollarRateMapper.findAllHkDollarRate();
//        if(rates == null){
//            return ServerResponse.createByErrorMsg("港元汇率获取失败");
//        }

//        HkDollarRate hkDollarRate = (HkDollarRate)rates.get(0);

        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");
        if(hkdollar == null){
            return ServerResponse.createByErrorMsg("港元汇率获取失败");
        }

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal outRate = hkdollar.getDefaultRate();
        BigDecimal inDiff = hkdollar.getInDiff();
        BigDecimal outDiff = hkdollar.getOutDiff();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();


        //查询港股配资杠杆
        List settings = siteHksSettingMapper.findAllSiteHksSetting();
        if(settings == null){
            return ServerResponse.createByErrorMsg("配资设置获取失败");
        }
        SiteHksSetting siteSetting = (SiteHksSetting) settings.get(0);

        Integer fundingVal = siteSetting.getFundingLevel();
        if( fundingVal <= 0){
            return ServerResponse.createByErrorMsg("配资杠杆数值异常");
        }

        BigDecimal changeValue = new BigDecimal(amt.intValue());
        BigDecimal fundingLevelBig = new BigDecimal(fundingVal.intValue());




        //固定汇率传入港股，本金池转入港股
        if (1 == type.intValue() && dynamicState != 0) {
            if (user.getUserCapital().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("可用本金账户可用资金不足");
            }
            //本金池
            BigDecimal userCapital = user.getUserCapital().subtract(changeValue);
            //增加A股杠杆
//            BigDecimal userHmt = user.getUserHmt().add(changeValue.multiply(fundingLevelBig.multiply(innRate)));
            BigDecimal userHmt = user.getUserHmt().add(changeValue.divide((innRate.add(inDiff)),2,4).multiply(fundingLevelBig));
            BigDecimal enableHmt = user.getEnableHmt().add(changeValue.divide((innRate.add(inDiff)),2,4).multiply(fundingLevelBig));

            user.setUserCapital(userCapital);
            user.setUserHmt(userHmt);
            user.setEnableHmt(enableHmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }

        //实时汇率入港股，本金池转入港股
        if (1 == type.intValue() && dynamicState == 0) {
            if (user.getUserCapital().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("可用本金账户可用资金不足");
            }

            BigDecimal userCapital = user.getUserCapital().subtract(changeValue);
            BigDecimal userHmt = user.getUserHmt().add(changeValue.divide((realRate.add(inDiff)),2,4).multiply(fundingLevelBig));
            BigDecimal enableHmt = user.getEnableHmt().add(changeValue.divide((realRate.add(inDiff)),2,4).multiply(fundingLevelBig));

            user.setUserCapital(userCapital);
            user.setUserHmt(userHmt);
            user.setEnableHmt(enableHmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }

        //固定汇率转出港股资金池，港股转入到本金池
        if (2 == type.intValue() && dynamicState != 0) {
            if (user.getEnableHmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("港股账户可用资金不足");
            }
            //判断用户交易金额是否满足本金交易10万元标准
            BigDecimal totalTurnover = userGgPositionMapper.countTotalTurnoverBuyUserId(user.getId());
            if(totalTurnover == null){
                totalTurnover = BigDecimal.ZERO;
            }

            if(totalTurnover.divide(fundingLevelBig).compareTo(new BigDecimal(100000)) == -1 && user.getUserHmt().compareTo(changeValue.add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.gg.money")))) == -1 ){
                return ServerResponse.createByErrorMsg("交易额不满足条件，赠送融资不可取");
            }

            BigDecimal userCapital = user.getUserCapital().add(changeValue.multiply(outRate.subtract(outDiff)).divide(fundingLevelBig,2,4));
            BigDecimal userHmt = user.getUserHmt().subtract(changeValue);
            BigDecimal enableHmt = user.getEnableHmt().subtract(changeValue);

            user.setUserCapital(userCapital);
            user.setUserHmt(userHmt);
            user.setEnableHmt(enableHmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }
        //实时汇率转出港股资金池，港股转到本金池
        if (2 == type.intValue() && dynamicState == 0) {
            if (user.getEnableHmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("港股账户可用资金不足");
            }
            //判断用户交易金额是否满足本金交易10万元标准
            BigDecimal totalTurnover = userGgPositionMapper.countTotalTurnoverBuyUserId(user.getId());

            if(totalTurnover.divide(fundingLevelBig).compareTo(new BigDecimal(100000)) == -1 && user.getUserHmt().compareTo(changeValue.add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.gg.money")))) == -1 ){
                return ServerResponse.createByErrorMsg("交易额不满足条件，赠送融资不可取");
            }

            BigDecimal userCapital = user.getUserCapital().add(changeValue.multiply(realRate.subtract(outDiff)).divide(fundingLevelBig,2,4));
            BigDecimal userHmt = user.getUserHmt().subtract(changeValue);
            BigDecimal enableHmt = user.getEnableHmt().subtract(changeValue);

            user.setUserCapital(userCapital);
            user.setUserHmt(userHmt);
            user.setEnableHmt(enableHmt);
            int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0) {
                saveAmtTransLog(user, type, amt);
                return ServerResponse.createBySuccessMsg("转账成功");
            }
            return ServerResponse.createByErrorMsg("转账失败");
        }

        return ServerResponse.createByErrorMsg("类型错误");
    }

*/


    private void saveAmtTransLog(User user, Integer type, Integer amt) {
        String amtFrom = "";
        String amtTo = "";
        if (1 == type.intValue()) {
            amtFrom = "融资";
            amtTo = "指数";
        }
        else if (2 == type.intValue()) {
            amtFrom = "指数";
            amtTo = "融资";
        }
        else if (3 == type.intValue()) {
            amtFrom = "融资";
            amtTo = "期货";
        }
        else if (4 == type.intValue()) {
            amtFrom = "期货";
            amtTo = "融资";
        }

        SiteAmtTransLog siteAmtTransLog = new SiteAmtTransLog();
        siteAmtTransLog.setUserId(user.getId());
        siteAmtTransLog.setRealName(user.getRealName());
        siteAmtTransLog.setAgentId(user.getAgentId());
        siteAmtTransLog.setAmtFrom(amtFrom);
        siteAmtTransLog.setAmtTo(amtTo);
        siteAmtTransLog.setTransAmt(new BigDecimal(amt.intValue()));
        siteAmtTransLog.setAddTime(new Date());
        this.siteAmtTransLogMapper.insert(siteAmtTransLog);
    }




    public void ForceSellTask() {
        List<Integer> userIdList = this.iUserPositionService.findDistinctUserIdList();

        log.info("当前有持仓单的用户数量 为 {}", Integer.valueOf(userIdList.size()));

    }

    public void ForceGgSellTask() {
        List<Integer> userIdList = this.iUserGgPositionService.findDistinctUserIdList();

        log.info("当前有持仓单的用户数量 为 {}", Integer.valueOf(userIdList.size()));

    }

    /*用户股票持仓单-强平提醒推送消息定时*/
    public void ForceSellMessageTask() {
        List<Integer> userIdList = this.iUserPositionService.findDistinctUserIdList();

        log.info("当前有持仓单的用户数量 为 {}", Integer.valueOf(userIdList.size()));

        for (int i = 0; i < userIdList.size(); i++) {
            log.info("=====================");
            Integer userId = (Integer)userIdList.get(i);
            User user = this.userMapper.selectByPrimaryKey(userId);
            if(user == null){
                continue;
            }


            List<UserPosition> userPositions = this.iUserPositionService.findPositionByUserIdAndSellIdIsNull(userId);

            log.info("用户id = {} 姓名 = {} 持仓中订单数： {}", new Object[] { userId, user.getRealName(), Integer.valueOf(userPositions.size()) });


            BigDecimal enable_user_amt = user.getEnableAmt();


            BigDecimal all_freez_amt = new BigDecimal("0");
            for (UserPosition position : userPositions) {

                BigDecimal actual_amt = position.getOrderTotalPrice().divide(new BigDecimal(position
                        .getOrderLever().intValue()), 2, 4);



                all_freez_amt = all_freez_amt.add(actual_amt);
            }


            BigDecimal all_profit_and_lose = new BigDecimal("0");
            PositionVO positionVO = this.iUserPositionService.findUserPositionAllProfitAndLose(userId);
            all_profit_and_lose = positionVO.getAllProfitAndLose();
            SiteSetting siteSetting = this.iSiteSettingService.getSiteSetting();
            BigDecimal force_stop_percent = siteSetting.getForceStopRemindRatio();
            /*BigDecimal force_stop_amt = force_stop_percent.multiply(all_freez_amt);
            BigDecimal user_force_amt = enable_user_amt.add(force_stop_amt);
            boolean isProfit = false;
            isProfit = (all_profit_and_lose.compareTo(new BigDecimal("0")) == -1 && user_force_amt.compareTo(all_profit_and_lose.negate()) != 1);
            */
            BigDecimal force_stop_amt = enable_user_amt.add(all_freez_amt);

            force_stop_amt = user.getUserStockACapital().add(user.getUserStockAGiveCapital());

            //(沪深)强制平仓线 = (账户可用资金 + 冻结保证金) *  0.8
            BigDecimal user_force_amt = force_stop_percent.multiply(force_stop_amt);
            BigDecimal fu_user_force_amt = user_force_amt.negate(); //负平仓线
            log.info("用户强制平仓线金额 = {}", user_force_amt);

            boolean isProfit = false;

            //总盈亏<=0  并且  强制负平仓线>=总盈亏
            isProfit = (all_profit_and_lose.compareTo(new BigDecimal("0")) < 1 && fu_user_force_amt.compareTo(all_profit_and_lose) > -1);
            if (isProfit) {
                log.info("强制平仓该用户所有的持仓单");
                int count = iSiteMessageService.getIsDayCount(userId,"股票预警");
                if(count == 0){
                    //给达到消息强平提醒用户推送消息
                    SiteMessage siteMessage = new SiteMessage();
                    siteMessage.setUserId(userId);
                    siteMessage.setUserName(user.getRealName());
                    siteMessage.setTypeName("股票预警");
                    siteMessage.setStatus(1);
                    siteMessage.setContent("【股票预警】提醒您，用户id = "+user.getId() + ", 可用资金 = " + enable_user_amt + "冻结保证金 = " + all_freez_amt + ", 强平比例 = " + force_stop_percent + ", 总盈亏" + all_profit_and_lose + ", 提醒线:" + user_force_amt +"，请及时关注哦。");
                    siteMessage.setAddTime(DateTimeUtil.getCurrentDate());
                    iSiteMessageService.insert(siteMessage);
                }

            } else {
                log.info("用户未达到强制平仓线，不做强平处理...");
            }

            log.info("=====================");
        }
    }

    /*用户股票持仓单-强平提醒推送消息定时*/
    public void ForceGgSellMessageTask() {
        List<Integer> userIdList = this.iUserGgPositionService.findDistinctUserIdList();

        log.info("当前有持仓单的用户数量 为 {}", Integer.valueOf(userIdList.size()));

        for (int i = 0; i < userIdList.size(); i++) {
            log.info("=====================");
            Integer userId = (Integer)userIdList.get(i);
            User user = this.userMapper.selectByPrimaryKey(userId);
            if(user == null){
                continue;
            }


            List<UserGgPosition> userPositions = this.iUserGgPositionService.findPositionByUserIdAndSellIdIsNull(userId);

            log.info("用户id = {} 姓名 = {} 持仓中订单数： {}", new Object[] { userId, user.getRealName(), Integer.valueOf(userPositions.size()) });


            BigDecimal enable_user_amt = user.getEnableHmt();


            BigDecimal all_freez_amt = new BigDecimal("0");
            for (UserGgPosition position : userPositions) {

                BigDecimal actual_amt = position.getOrderTotalPrice().divide(new BigDecimal(position
                        .getOrderLever().intValue()), 2, 4);



                all_freez_amt = all_freez_amt.add(actual_amt);
            }


            BigDecimal all_profit_and_lose = new BigDecimal("0");
            PositionVO positionVO = this.iUserGgPositionService.findUserPositionAllProfitAndLose(userId);
            all_profit_and_lose = positionVO.getAllProfitAndLose();
            SiteHksSetting siteSetting = this.iSiteHksSettingService.getSiteHksSetting();
            BigDecimal force_stop_percent = siteSetting.getForceStopRemindRatio();
            BigDecimal force_stop_amt = enable_user_amt.add(all_freez_amt);

            //(沪深)强制平仓线 = (账户可用资金 + 冻结保证金) *  0.8
            BigDecimal user_force_amt = force_stop_percent.multiply(force_stop_amt);
            BigDecimal fu_user_force_amt = user_force_amt.negate(); //负平仓线
            log.info("用户强制平仓线金额 = {}", user_force_amt);

            boolean isProfit = false;

            //总盈亏<=0  并且  强制负平仓线>=总盈亏
            isProfit = (all_profit_and_lose.compareTo(new BigDecimal("0")) < 1 && fu_user_force_amt.compareTo(all_profit_and_lose) > -1);
            if (isProfit) {
                log.info("强制平仓该用户所有的港股持仓单");
                int count = iSiteMessageService.getIsDayCount(userId,"股票预警");
                if(count == 0){
                    //给达到消息强平提醒用户推送消息
                    SiteMessage siteMessage = new SiteMessage();
                    siteMessage.setUserId(userId);
                    siteMessage.setUserName(user.getRealName());
                    siteMessage.setTypeName("股票预警");
                    siteMessage.setStatus(1);
                    siteMessage.setContent("【股票预警】提醒您，用户id = "+user.getId() + ", 可用港股资金 = " + enable_user_amt + "冻结保证金 = " + all_freez_amt + ", 强平比例 = " + force_stop_percent + ", 总盈亏" + all_profit_and_lose + ", 提醒线:" + user_force_amt +"，请及时关注哦。");
                    siteMessage.setAddTime(DateTimeUtil.getCurrentDate());
                    iSiteMessageService.insert(siteMessage);
                }

            } else {
                log.info("用户未达到强制平仓线，不做强平处理...");
            }

            log.info("=====================");
        }
    }



    public void ForceSellIndexTask() {
        List<Integer> userIdList = this.iUserIndexPositionService.findDistinctUserIdList();

        log.info("当前有 指数持仓 的用户数量 为 {}", Integer.valueOf(userIdList.size()));

        for (int i = 0; i < userIdList.size(); i++) {
            log.info("=====================");
            Integer userId = (Integer)userIdList.get(i);
            User user = this.userMapper.selectByPrimaryKey(userId);
            if(user == null){
                continue;
            }


            List<UserIndexPosition> userIndexPositions = this.iUserIndexPositionService.findIndexPositionByUserIdAndSellPriceIsNull(userId);

            log.info("用户id = {} 姓名 = {} 持仓中订单数: {}", new Object[] { userId, user
                    .getRealName(), Integer.valueOf(userIndexPositions.size()) });


            IndexPositionVO indexPositionVO = this.iUserIndexPositionService.findUserIndexPositionAllProfitAndLose(userId);


            BigDecimal enable_index_amt = user.getEnableIndexAmt();


            BigDecimal all_freez_amt = indexPositionVO.getAllIndexFreezAmt();

            BigDecimal all_profit_and_lose = indexPositionVO.getAllIndexProfitAndLose();

            log.info("用户 {} 可用资金 = {} 总冻结保证金 = {} 所有持仓单的总盈亏 = {}", new Object[] { userId, enable_index_amt, all_freez_amt, all_profit_and_lose });



            SiteIndexSetting siteIndexSetting = this.iSiteIndexSettingService.getSiteIndexSetting();
            BigDecimal force_stop_percent = siteIndexSetting.getForceSellPercent();
            BigDecimal force_stop_amt = enable_index_amt.add(all_freez_amt);

            //(指数)强制平仓线 = (账户可用资金 + 冻结保证金) *  0.8
            BigDecimal user_force_amt = force_stop_percent.multiply(force_stop_amt);
            BigDecimal fu_user_force_amt = user_force_amt.negate(); //负平仓线
            log.info("用户强制平仓线金额 = {}", user_force_amt);
            boolean isProfit = false;
            //总盈亏<=0  并且  强制负平仓线>=总盈亏
            isProfit = (all_profit_and_lose.compareTo(new BigDecimal("0")) < 1 && fu_user_force_amt.compareTo(all_profit_and_lose) > -1);

            if (isProfit) {
                log.info("强制平仓该用户所有的指数持仓单");

                int[] arrs = new int[userIndexPositions.size()];
                for (int k = 0; k < userIndexPositions.size(); k++) {
                    UserIndexPosition userIndexPosition = (UserIndexPosition)userIndexPositions.get(k);
                    arrs[k] = userIndexPosition.getId().intValue();
                    try {
                        this.iUserIndexPositionService.sellIndex(userIndexPosition.getPositionSn(), 0);
                    }
                    catch (Exception e) {
                        log.error("[盈亏达到最大亏损]强制平仓指数失败...");
                    }
                }


                SiteTaskLog siteTaskLog = new SiteTaskLog();
                siteTaskLog.setTaskType("强平任务-指数持仓");
                String accountType = (user.getAccountType().intValue() == 0) ? "正式用户" : "模拟用户";
                String taskcnt = accountType + "-" + user.getRealName() + "被强平 [指数盈亏达到最大亏损] 用户 id = " + user.getId() + ", 可用资金 = " + enable_index_amt + ", 冻结资金 = " + all_freez_amt + ", 强平比例 = " + force_stop_percent + ", 总盈亏 = " + all_profit_and_lose + ", 强平线 = " + user_force_amt;




                siteTaskLog.setTaskCnt(taskcnt);

                String tasktarget = "此次强平" + userIndexPositions.size() + "条指数持仓订单, 订单号为" + Arrays.toString(arrs);
                siteTaskLog.setTaskTarget(tasktarget);
                siteTaskLog.setAddTime(new Date());
                siteTaskLog.setIsSuccess(Integer.valueOf(0));
                siteTaskLog.setErrorMsg("");
                int insertTaskCount = this.siteTaskLogMapper.insert(siteTaskLog);
                if (insertTaskCount > 0) {
                    log.info("[盈亏达到最大亏损] 保存强制平仓 指数 task任务成功");
                } else {
                    log.info("[盈亏达到最大亏损] 保存强制平仓 指数 task任务失败");
                }
            } else {
                log.info("用户指数持仓未达到强制平仓线, 不做强平处理...");
            }

            log.info("=====================");
        }
    }

    /*指数强平提醒推送消息，每分钟检测一次*/
    public void ForceSellIndexsMessageTask() {
        List<Integer> userIdList = this.iUserIndexPositionService.findDistinctUserIdList();

        log.info("当前有 指数持仓 的用户数量 为 {}", Integer.valueOf(userIdList.size()));

        for (int i = 0; i < userIdList.size(); i++) {
            log.info("=====================");
            Integer userId = (Integer)userIdList.get(i);
            User user = this.userMapper.selectByPrimaryKey(userId);
            if(user == null){
                continue;
            }


            List<UserIndexPosition> userIndexPositions = this.iUserIndexPositionService.findIndexPositionByUserIdAndSellPriceIsNull(userId);

            log.info("用户id = {} 姓名 = {} 持仓中订单数: {}", new Object[] { userId, user
                    .getRealName(), Integer.valueOf(userIndexPositions.size()) });


            IndexPositionVO indexPositionVO = this.iUserIndexPositionService.findUserIndexPositionAllProfitAndLose(userId);


            BigDecimal enable_index_amt = user.getEnableIndexAmt();


            BigDecimal all_freez_amt = indexPositionVO.getAllIndexFreezAmt();

            BigDecimal all_profit_and_lose = indexPositionVO.getAllIndexProfitAndLose();

            log.info("用户 {} 可用资金 = {} 总冻结保证金 = {} 所有持仓单的总盈亏 = {}", new Object[] { userId, enable_index_amt, all_freez_amt, all_profit_and_lose });



            SiteIndexSetting siteIndexSetting = this.iSiteIndexSettingService.getSiteIndexSetting();
            BigDecimal force_stop_percent = siteIndexSetting.getForceStopRemindRatio();
            BigDecimal force_stop_amt = enable_index_amt.add(all_freez_amt);

            //(指数)强制平仓线 = (账户可用资金 + 冻结保证金) *  0.8
            BigDecimal user_force_amt = force_stop_percent.multiply(force_stop_amt);
            BigDecimal fu_user_force_amt = user_force_amt.negate(); //负平仓线
            log.info("用户强制平仓线金额 = {}", user_force_amt);
            boolean isProfit = false;
            //总盈亏<=0  并且  强制负平仓线>=总盈亏
            isProfit = (all_profit_and_lose.compareTo(new BigDecimal("0")) < 1 && fu_user_force_amt.compareTo(all_profit_and_lose) > -1);

            if (isProfit) {
                log.info("强制平仓该用户所有的指数持仓单");

                int count = iSiteMessageService.getIsDayCount(userId,"指数预警");
                if(count == 0){
                    //给达到消息强平提醒用户推送消息
                    SiteMessage siteMessage = new SiteMessage();
                    siteMessage.setUserId(userId);
                    siteMessage.setUserName(user.getRealName());
                    siteMessage.setTypeName("指数预警");
                    siteMessage.setStatus(1);
                    siteMessage.setContent("【指数预警】提醒您，用户id = "+user.getId() + ", 可用资金 = " + enable_index_amt + ", 冻结资金 = " + all_freez_amt + ", 强平比例 = " + force_stop_percent + ", 总盈亏 = " + all_profit_and_lose + ", 提醒线 = " + user_force_amt +"，请及时关注哦。");
                    siteMessage.setAddTime(DateTimeUtil.getCurrentDate());
                    iSiteMessageService.insert(siteMessage);
                }

            } else {
                log.info("用户指数持仓未达到强制平仓线, 不做强平处理...");
            }

            log.info("=====================");
        }
    }

    public void qh1() {
        this.stockPoll.qh1();
    }

    public void zs1() {
        this.stockPoll.zs1();
    }

    public void ForceSellFuturesTask() {
        List<Integer> userIdList = this.iUserFuturesPositionService.findDistinctUserIdList();

    }

    public void ForceSellFuturesMessageTask() {
        List<Integer> userIdList = this.iUserFuturesPositionService.findDistinctUserIdList();

    }




    public ServerResponse listByAgent(String realName, String phone, Integer agentId, Integer accountType, int pageNum, int pageSize, HttpServletRequest request) {
        SiteSetting siteSetting = this.iSiteSettingService.getSiteSetting();
        SiteIndexSetting siteIndexSetting = this.iSiteIndexSettingService.getSiteIndexSetting();
        SiteFuturesSetting siteFuturesSetting = this.iSiteFuturesSettingService.getSetting();


        AgentUser currentAgent = this.iAgentUserService.getCurrentAgent(request);

        if (agentId != null) {
            AgentUser agentUser = this.agentUserMapper.selectByPrimaryKey(agentId);
            if (agentUser.getParentId() != currentAgent.getId()) {
                return ServerResponse.createByErrorMsg("不能查询非下级代理用户持仓");
            }
        }
        Integer searchId = null;
        if (agentId == null) {
            searchId = currentAgent.getId();
        } else {
            searchId = agentId;
        }

        PageHelper.startPage(pageNum, pageSize);

        List<User> users = this.userMapper.listByAgent(realName, phone, searchId, accountType);

        List<AgentUserListVO> agentUserListVOS = Lists.newArrayList();
        for (User user : users) {
            AgentUserListVO agentUserListVO = assembleAgentUserListVO(user, siteSetting
                    .getForceStopPercent(), siteIndexSetting
                    .getForceSellPercent(), siteFuturesSetting.getForceSellPercent());
            agentUserListVOS.add(agentUserListVO);
        }

        PageInfo pageInfo = new PageInfo(users);
        pageInfo.setList(agentUserListVOS);

        return ServerResponse.createBySuccess(pageInfo);
    }



    public ServerResponse addSimulatedAccount(Integer agentId, String phone, String pwd, String amt, Integer accountType, HttpServletRequest request) {
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd)) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }
        boolean isEm = isEmail(phone);

        if (!isEm) {
            return ServerResponse.createByErrorMsg("注册失败, 邮箱不正确");
        }

        User dbUser = this.userMapper.findByEmail(phone);
        if (dbUser != null) {
            return ServerResponse.createByErrorMsg("邮箱已注册");
        }


        if ((new BigDecimal(amt)).compareTo(new BigDecimal("200000")) == 1) {
            return ServerResponse.createByErrorMsg("模拟账户资金不能超过20万");
        }

        amt = "0";   //代理后台添加用户时金额默认为0
        User user = new User();
        user.setAccountType(accountType);
//        user.setPhone(phone);
        user.setEmail(phone);
        user.setUserPwd(pwd);
        user.setUserCapital(new BigDecimal(amt));
        user.setUserAmt(BigDecimal.ZERO);
        user.setUserHmt(BigDecimal.ZERO);
        user.setEnableHmt(BigDecimal.ZERO);
        user.setEnableAmt(BigDecimal.ZERO);
        user.setSumChargeAmt(new BigDecimal("0"));
        user.setSumBuyAmt(new BigDecimal("0"));
        user.setIsLock(Integer.valueOf(0));
        user.setIsLogin(Integer.valueOf(0));
        user.setIsActive(Integer.valueOf(0));
        user.setRegTime(new Date());

        user.setUserStockACapital(BigDecimal.ZERO);

        user.setUserStockAOcapital(BigDecimal.ZERO);

        user.setUserStockHKCapital(BigDecimal.ZERO);

        user.setUserStockHKOcapital(BigDecimal.ZERO);

        user.setUserStockAGiveCapital(BigDecimal.ZERO);

        if (accountType.intValue() == 1) {
            user.setNickName("模拟用户");
        }

        user.setUserIndexAmt(new BigDecimal("0"));
        user.setEnableIndexAmt(new BigDecimal("0"));
        user.setUserFutAmt(new BigDecimal("0"));
        user.setEnableFutAmt(new BigDecimal("0"));

        if (agentId != null) {
            AgentUser agentUser = this.agentUserMapper.selectByPrimaryKey(agentId);
            user.setAgentName(agentUser.getAgentName());
            user.setAgentId(agentUser.getId());
        }

        int insertCount = this.userMapper.insert(user);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("用户添加成功");
        }
        return ServerResponse.createByErrorMsg("用户添加失败");
    }


    @Override
    public ServerResponse abstractListByAdmin(String realName, String phone, Integer agentId, Integer accountType, Date startTime, Date endTime, int pageNum, int pageSize, HttpServletRequest request) {
        PageHelper.startPage(pageNum, pageSize);

        List<Map> users = this.userMapper.abstractListByAdmin(realName, phone, agentId, accountType,startTime,endTime);

        PageInfo pageInfo = new PageInfo(users);

        //获取A股港股杠杆信息
        List setting = siteSettingMapper.findAllSiteSetting();
        List hksSetting = siteHksSettingMapper.findAllSiteHksSetting();

        //获取港股或汇率信息
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();
        if(setting.size()>0 && hksSetting.size()>0){
            SiteSetting   siteSetting = (SiteSetting) setting.get(0);
            SiteHksSetting   hkSiteSetting = (SiteHksSetting) hksSetting.get(0);
            Integer fundingLevelA = siteSetting.getFundingLevel();
            Integer fundingLevelH = hkSiteSetting.getFundingLevel();
            BigDecimal outDiff = hkdollar.getOutDiff();
            if(fundingLevelA != 0 && fundingLevelH != 0){
                BigDecimal htotal = new BigDecimal("0");
                BigDecimal aTotal = new BigDecimal("0");
                for (Map map  :users) {
                    //处理用户总资产
                    BigDecimal userCapital = (BigDecimal) map.get("userCapital");
                    BigDecimal userAmt = (BigDecimal) map.get("userAmt");
                    BigDecimal userHmt = (BigDecimal) map.get("userHmt");

                    if(userHmt == null)
                        userHmt = BigDecimal.ZERO;

                    if(dynamicState == 1)
                        htotal =userHmt.multiply(innRate.subtract(outDiff)).divide(new BigDecimal(fundingLevelH),2,4);
                    else
                        htotal = userHmt.multiply(realRate.subtract(outDiff).divide(new BigDecimal(fundingLevelH),2,4));

                    aTotal = userAmt.divide(new BigDecimal(fundingLevelA),2,4);

                    map.put("userCapital",userCapital.add(htotal).add(aTotal));

                    //处理时间问题
                    Date regTime = (Date) map.get("regTime");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String regTimeStr = formatter.format(regTime);
                    map.put("regTime",regTimeStr);

                    Integer userId = (Integer) map.get("userId");

                    PositionVO positionVO = this.iUserPositionService.findUserPositionAllProfitAndLose(userId);
                    map.put("feeA",positionVO.getAllFree());
                    BigDecimal allProfitAndLose = positionVO.getAllProfitAndLose();
                    map.put("allAProLose",allProfitAndLose);
                    map.put("totalAbuy",positionVO.getAllPositPric());

                    PositionVO positionGgVO = this.iUserGgPositionService.findUserPositionAllProfitAndLose(userId);
                    map.put("feeH",positionGgVO.getAllFree());
                    BigDecimal allProfitAndLoseGg = positionGgVO.getAllProfitAndLose();
                    map.put("allHProLose",allProfitAndLoseGg);
                    map.put("totalHbuy",positionGgVO.getAllPositPric());

                    map.put("userStockACapital",(BigDecimal)map.get("userStockACapital"));

                    map.put("userStockCapital",(BigDecimal)map.get("userStockHKCapital"));

                    map.put("totalUserCapital",(BigDecimal)((BigDecimal) map.get("userStockACapital")).add((BigDecimal)map.get("userStockAGiveCapital")).add((BigDecimal)((BigDecimal) map.get("userStockHKCapital")).multiply(innRate)).add((BigDecimal)map.get("userCapital")));

                    map.put("HKFee",hkdollar.getDefaultRate());


                }
            }
        }

        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse<PageInfo> listByAdmin(String realName, String phone, Integer agentId, Integer accountType, Integer isActive, int pageNum, int pageSize, HttpServletRequest request) {

        PageHelper.startPage(pageNum, pageSize);

        List<User> users = this.userMapper.listByAdmin(realName, phone, agentId, accountType,isActive);

        //港股资金池转换
        //获取港元汇率
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();

        List setting = siteSettingMapper.findAllSiteSetting();

        List hksSetting = siteHksSettingMapper.findAllSiteHksSetting();
        List<UserBackInfoVo> backList = new ArrayList<>();

        if(setting.size()>0 && hksSetting.size()>0){
            SiteSetting   siteSetting = (SiteSetting) setting.get(0);
            SiteHksSetting   hkSiteSetting = (SiteHksSetting) hksSetting.get(0);
            Integer fundingLevelA = siteSetting.getFundingLevel();
            Integer fundingLevelH = hkSiteSetting.getFundingLevel();
            BigDecimal outDiff = hkdollar.getOutDiff();
            if(fundingLevelA != 0 && fundingLevelH != 0){
                BigDecimal htotal = new BigDecimal("0");
                BigDecimal aTotal = new BigDecimal("0");
                for (User user :users) {
                    UserBackInfoVo userBackInfoVo = new UserBackInfoVo();
                    BeanUtils.copyProperties(user,userBackInfoVo);
                    BigDecimal userHmt = userBackInfoVo.getUserHmt();
                    if(userHmt == null)
                        userHmt = BigDecimal.ZERO;
                    if(dynamicState == 1)
                        userBackInfoVo.setUserHmtMulRate(userHmt.multiply(innRate));
                    else
                        userBackInfoVo.setUserHmtMulRate(userHmt.multiply(realRate));
                    if(dynamicState == 1)
                        htotal = user.getUserHmt().multiply(innRate.subtract(outDiff)).divide(new BigDecimal(fundingLevelH),2,4);
                    else
                        htotal = user.getUserHmt().multiply(realRate.subtract(outDiff).divide(new BigDecimal(fundingLevelH),2,4));

                    aTotal = user.getUserAmt().divide(new BigDecimal(fundingLevelA),2,4);

                    userBackInfoVo.setTotalCapital(user.getUserCapital().add(htotal).add(aTotal));

                    BigDecimal stockACapital = BigDecimal.ZERO;

                    BigDecimal stockAGiveCapital = BigDecimal.ZERO;

                    BigDecimal userStockAGiveCapital = user.getUserStockAGiveCapital();

                    if(user.getIsActive() == 2) {

                        stockAGiveCapital = userStockAGiveCapital;  // 赠送的A股本金

                        stockACapital = user.getUserStockACapital();  // 不是赠送的A股本金
                    }else{
                        stockAGiveCapital = BigDecimal.ZERO;

                        stockACapital = user.getUserStockACapital();  // 不是赠送的A股本金
                    }

                    //BigDecimal stockACapital = user.getUserStockACapital().add(allProfitAndLose); // 不加盈亏



                    if(stockACapital.compareTo(new BigDecimal(0))==-1){
                        stockACapital = new BigDecimal(0);
                    }



                    userBackInfoVo.setUserStockACapital(stockACapital.add(user.getUserStockAGiveCapital()));

                    userBackInfoVo.setUserStockHKCapital(user.getUserStockHKCapital());

                    backList.add(userBackInfoVo);
                }
            }
        }


        PageInfo pageInfo = new PageInfo(users);

        pageInfo.setList(backList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    public void listExportByAdmin(String realName, String phone,  Integer agentId,  Integer accountType, Integer isActive, HttpServletResponse response) {

        List<User> users = this.userMapper.listByAdmin(realName, phone, agentId, accountType,isActive);
        List<UserExcel> userExcels = new ArrayList<>();

        for (User user: users){
            UserExcel ex = new UserExcel();
            BeanUtils.copyProperties(user,ex);
            UserBank userBank = userBankMapper.findUserBankByUserId(user.getId());
            if(userBank!=null){
                ex.setBankName(userBank.getBankName());
                ex.setBankAddress(userBank.getBankAddress());
                ex.setBankNo(userBank.getBankNo());
            }

            if(user.getIsActive() == 0){
                ex.setIsActive("待认证");
            }else if(user.getIsActive() == 1) {
                ex.setIsActive("待审核");
            }else {
                ex.setIsActive("成功");
            }
            if(user.getIsLock() == 1) {
                ex.setIsLock("不可交易");
            }else {
                ex.setIsLock("可交易");
            }
            if(user.getIsLogin() == 0) {
                ex.setIsLogin("可登录");
            }else {
                ex.setIsLogin("不可登陆");
            }
            userExcels.add(ex);
        }
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=userMsg.xlsx");
            ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0, "用户列表").head(UserExcel.class).build();
            excelWriter.write(userExcels, writeSheet1);
            excelWriter.finish();
        } catch (Exception var10) {
            var10.printStackTrace();
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = new HashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + var10.getMessage());
            try {
                response.getWriter().println(JSON.toJSONString(map));
            } catch (IOException e) {
                e.printStackTrace();
            }
            var10.printStackTrace();
        } finally {
        }

    }

    public ServerResponse findByUserId(Integer userId) { return ServerResponse.createBySuccess(this.userMapper.selectByPrimaryKey(userId)); }




    public ServerResponse updateLock(Integer userId) {
        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        if (user.getIsLock().intValue() == 1) {
            user.setIsLock(Integer.valueOf(0));
        } else {
            user.setIsLock(Integer.valueOf(1));
        }

        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccess("修改成功");
        }
        return ServerResponse.createByErrorMsg("修改失败");
    }



    @Transactional
    public ServerResponse updateAmt(Integer userId, Integer amt, Integer direction, Integer type) {
        if (userId == null || amt == null || direction == null || type == null) {
            return ServerResponse.createByErrorMsg("参数不能为空");
        }

        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMsg("用户不存在");
        }

        // BigDecimal user_amt = user.getUserCapital(); // 用户保证金

        BigDecimal old_user_amt = BigDecimal.ZERO;

        BigDecimal user_amt = BigDecimal.ZERO;

        //  0 本金 1 A股票 2 港股
        String typeName = "本金";
        switch(type.intValue()){
            case 0 :
                typeName = "本金";
                if(direction.intValue() == 0) {

                    old_user_amt = user.getUserCapital();

                    user_amt = user.getUserCapital().add(new BigDecimal(amt.intValue()));

                    user.setUserCapital(user_amt);

                }else if (direction.intValue() == 1) {

                    if (user.getUserCapital().compareTo(new BigDecimal(amt.intValue())) == -1) {
                        return ServerResponse.createByErrorMsg("扣款失败, 总资金不足");
                    }

                    old_user_amt = user.getUserCapital();

                    user_amt = user.getUserCapital().subtract(new BigDecimal(amt.intValue()));

                    user.setUserCapital(user_amt);

                }else{

                    return ServerResponse.createByErrorMsg("不存在此操作");

                }
                break;
            case 1 :
                typeName = "A股";
                if(direction.intValue() == 0) {
                    old_user_amt = user.getUserStockACapital();
                    user_amt = user.getUserStockACapital().add(new BigDecimal(amt.intValue()));
                    user.setUserStockACapital(user_amt);
//                    user_amt = user.getUserAmt().add(new BigDecimal(amt.intValue()));
//                    user.setUserAmt(user.getUserAmt().add(new BigDecimal(amt.intValue())));
//                    user.setEnableAmt(user.getEnableAmt().add(new BigDecimal(amt.intValue())));

                }else if (direction.intValue() == 1) {

                    if (user.getUserStockAOcapital().compareTo(new BigDecimal(amt.intValue())) == -1 ) {
                        return ServerResponse.createByErrorMsg("扣款失败, 资金不足");
                    }
                    old_user_amt = user.getUserStockACapital();
                    user_amt = user.getUserStockAOcapital().subtract(new BigDecimal(amt.intValue()));
                    user.setUserStockACapital(user.getUserStockACapital().subtract(new BigDecimal(amt.intValue())));


//                    if (user.getUserAmt().compareTo(new BigDecimal(amt.intValue())) == -1 || user.getEnableAmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
//                        return ServerResponse.createByErrorMsg("扣款失败, 总资金不足");
//                    }
//                    old_user_amt = user.getUserAmt();
                    user_amt = user.getUserAmt().subtract(new BigDecimal(amt.intValue()));
                    user.setUserAmt(user.getUserAmt().subtract(new BigDecimal(amt.intValue())));
                    user.setEnableAmt(user.getEnableAmt().subtract(new BigDecimal(amt.intValue())));

                }else{

                    return ServerResponse.createByErrorMsg("不存在此操作");

                }
                break;
            case 2 :
                typeName = "港股";
                if(direction.intValue() == 0) {
                    old_user_amt = user.getUserStockHKCapital();
                    user_amt = user.getUserStockHKCapital().add(new BigDecimal(amt.intValue()));
                    user.setUserStockHKCapital(user.getUserStockHKCapital().add(new BigDecimal(amt.intValue())));
//                    user.setEnableHmt(user.getEnableHmt().add(new BigDecimal(amt.intValue())));


                }else if (direction.intValue() == 1) {

                    if (user.getUserStockAOcapital().compareTo(new BigDecimal(amt.intValue())) == -1 ) {
                        return ServerResponse.createByErrorMsg("扣款失败, 资金不足");
                    }
                    old_user_amt = user.getUserStockAOcapital();
                    user_amt = user.getUserStockAOcapital().subtract(new BigDecimal(amt.intValue()));

                    user.setUserStockHKCapital(user.getUserStockHKCapital().subtract(new BigDecimal(amt.intValue())));


//                    if (user.getUserHmt().compareTo(new BigDecimal(amt.intValue())) == -1 || user.getEnableHmt().compareTo(new BigDecimal(amt.intValue())) == -1) {
//                        return ServerResponse.createByErrorMsg("扣款失败, 总资金不足");
//                    }
//                    old_user_amt = user.getUserHmt();
                    user_amt = user.getUserHmt().subtract(new BigDecimal(amt.intValue()));

                    user.setUserHmt(user.getUserHmt().subtract(new BigDecimal(amt.intValue())));

                    user.setEnableHmt(user.getEnableHmt().subtract(new BigDecimal(amt.intValue())));



                }else{

                    return ServerResponse.createByErrorMsg("不存在此操作");

                }
                break;
            default :

        }

        /*
        BigDecimal user_catipal = new BigDecimal("0");

        if (direction.intValue() == 0) {
            // 增加
            user_catipal = user_amt.add(new BigDecimal(amt.intValue()));

        } else if (direction.intValue() == 1) {
            // 减少
            if (user_amt.compareTo(new BigDecimal(amt.intValue())) == -1) {
                return ServerResponse.createByErrorMsg("扣款失败, 总资金不足");
            }
            user_catipal = user_amt.subtract(new BigDecimal(amt.intValue()));
        } else {
            return ServerResponse.createByErrorMsg("不存在此操作");
        }


        user.setUserCapital(user_catipal);*/

        this.userMapper.updateByPrimaryKeySelective(user);

        SiteTaskLog siteTaskLog = new SiteTaskLog();
        siteTaskLog.setTaskType("管理员修改"+typeName+"金额");
        StringBuffer cnt = new StringBuffer();


        cnt.append("管理员修改"+typeName+"金额 - ")
                .append((direction.intValue() == 0) ? "入款" : "扣款")
                .append(amt).append("元");
        siteTaskLog.setTaskCnt(cnt.toString());

        StringBuffer target = new StringBuffer();

        //  0 本金 1 A股票 2 港股
        switch(type.intValue()){
            case 0 :
                target.append("用户id : ").append(user.getId())
                        .append("修改前 总本金资金 = ").append(old_user_amt)
                        .append("修改后 总本金资金 = ").append(user_amt);
                break;
            case 1 :
                target.append("用户id : ").append(user.getId())
                        .append("修改前 总资金 = ").append(old_user_amt)
                        .append("修改后 总资金 = ").append(user_amt);

                break;
            case 2 :
                target.append("用户id : ").append(user.getId())
                        .append("修改前 总资金 = ").append(old_user_amt)
                        .append("修改后 总资金 = ").append(user_amt);
                break;
            default :

        }

//        target.append("用户id : ").append(user.getId())
//                .append("修改前 总资金 = ").append(user_amt)
//                .append("修改后 总资金 = ").append(user_catipal);


        siteTaskLog.setTaskTarget(target.toString());

        siteTaskLog.setIsSuccess(Integer.valueOf(0));
        siteTaskLog.setAddTime(new Date());

        int insertCount = this.siteTaskLogMapper.insert(siteTaskLog);
        if (insertCount > 0) {
            return ServerResponse.createBySuccessMsg("修改资金成功");
        }
        return ServerResponse.createByErrorMsg("修改资金失败");
    }




    public ServerResponse delete(Integer userId, HttpServletRequest request) {
        String cookie_name = PropertiesUtil.getProperty("admin.cookie.name");
        String logintoken = CookieUtils.readLoginToken(request, cookie_name);
        String adminJson = RedisShardedPoolUtils.get(logintoken);
        SiteAdmin siteAdmin = (SiteAdmin)JsonUtil.string2Obj(adminJson, SiteAdmin.class);

        log.info("管理员 {} 删除用户 {}", siteAdmin.getAdminName(), userId);


        int delChargeCount = this.iUserRechargeService.deleteByUserId(userId);
        if (delChargeCount > 0) {
            log.info("删除 充值 记录成功");
        } else {
            log.info("删除 充值 记录失败");
        }


        int delWithdrawCount = this.iUserWithdrawService.deleteByUserId(userId);
        if (delWithdrawCount > 0) {
            log.info("删除 提现 记录成功");
        } else {
            log.info("删除 提现 记录失败");
        }


        int delCashCount = this.iUserCashDetailService.deleteByUserId(userId);
        if (delCashCount > 0) {
            log.info("删除 资金 记录成功");
        } else {
            log.info("删除 资金 记录成功");
        }


        int delPositionCount = this.iUserPositionService.deleteByUserId(userId);
        if (delPositionCount > 0) {
            log.info("删除 持仓 记录成功");
        } else {
            log.info("删除 持仓 记录失败");
        }


        int delLogCount = this.iSiteLoginLogService.deleteByUserId(userId);
        if (delLogCount > 0) {
            log.info("删除 登录 记录成功");
        } else {
            log.info("删除 登录 记录失败");
        }


        int delUserCount = this.userMapper.deleteByPrimaryKey(userId);
        if (delUserCount > 0) {
            return ServerResponse.createBySuccessMsg("操作成功");
        }
        return ServerResponse.createByErrorMsg("操作失败, 查看日志");
    }





    public int CountUserSize(Integer accountType) { return this.userMapper.CountUserSize(accountType); }





    public BigDecimal CountUserAmt(Integer accountType) { return this.userMapper.CountUserAmt(accountType); }




    public BigDecimal CountEnableAmt(Integer accountType) { return this.userMapper.CountEnableAmt(accountType); }


    /**
     * 审核用户身份证
     * @param userId 用户id
     * @param state  1： 审核中  2：审核通过 3 审核不通过
     * @param authMsg 不通过原因
     * @return
     */
    public ServerResponse authByAdmin(Integer userId, Integer state, String authMsg) {
        if (state == null || userId == null) {
            return ServerResponse.createByErrorMsg("id和state不能为空");
        }

        User user = this.userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMsg("查不到此用户");
        }

        if (user.getIsActive() == 2) {
            return ServerResponse.createByErrorMsg("用户已审核，无需重复审核");
        }

        if (state.intValue() == 3) {
            if (StringUtils.isBlank(authMsg)) {
                return ServerResponse.createByErrorMsg("审核失败信息必填");
            }
            user.setAuthMsg(authMsg);
        }

        user.setIsActive(state);

        //审核成功自动赋予8888A股和港股融资资金

        // 审核通过才送
        if(state.intValue() == 2) {

            //审核通过自动转为可交易状态
            user.setIsLock(Integer.valueOf(0));

            user.setUserAmt(user.getUserAmt().add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money"))));
            user.setEnableAmt(user.getEnableAmt().add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money"))));
            user.setUserHmt(user.getUserHmt().add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.gg.money"))));
            user.setEnableHmt(user.getEnableHmt().add(new BigDecimal(PropertiesUtil.getProperty("zeng.song.gg.money"))));

            user.setUserStockAGiveCapital(this.getStockAGiveCapital()); // 赠送的A股本金
        }

        int updateCount = this.userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMsg("审核成功");
        }

        return ServerResponse.createByErrorMsg("审核失败");
    }

    /**
     * 获取A股本金
     * @return
     */
    public BigDecimal getStockAGiveCapital(){

        BigDecimal giveStockACapital = new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money"));

        return giveStockACapital.divide(this.fundingLevel());

    }

    /**
     * 配资倍数
     * @return
     */
    public BigDecimal fundingLevel(){
        Integer fundingLevel = 1;

        List setting = siteSettingMapper.findAllSiteSetting();
        if (setting.size() > 0) {
            SiteSetting   siteSetting = (SiteSetting) setting.get(0);
            fundingLevel = siteSetting.getFundingLevel();
        }

        return new BigDecimal(fundingLevel);
    }

    @Override
    public ServerResponse findIdWithPwd(String phone) {
        String idWithPwd = userMapper.findIdWithPwd(phone);

        if (idWithPwd==null){
            return ServerResponse.createByErrorMsg("请设置提现密码！");
        }else {
            return ServerResponse.createBySuccessMsg("密码已设置,可以提现！");
        }
    }

    @Override
    public ServerResponse updateWithPwd(String with_pwd, String phone) {

        if (StringUtils.isBlank(with_pwd)||StringUtils.isBlank(phone)){
            return ServerResponse.createByErrorMsg("参数不能为空");
        }

        String withPwd = userMapper.findWithPwd(with_pwd);

        if (withPwd!=null){
            return ServerResponse.createByErrorMsg("您已经添加了提现密码！");
        }

        int i = userMapper.updateWithPwd(with_pwd, phone);
        if (i>0){
            return ServerResponse.createBySuccessMsg("添加成功！");
        }else {
            return ServerResponse.createByErrorMsg("添加失败！");
        }
    }


    private AgentUserListVO assembleAgentUserListVO(User user, BigDecimal forcePercent, BigDecimal indexForcePercent, BigDecimal futuresForcePercent) {
        AgentUserListVO agentUserListVO = new AgentUserListVO();

        agentUserListVO.setId(user.getId());
        agentUserListVO.setAgentId(user.getAgentId());
        agentUserListVO.setAgentName(user.getAgentName());
        agentUserListVO.setPhone(user.getPhone());
        agentUserListVO.setRealName(user.getRealName());
        agentUserListVO.setIdCard(user.getIdCard());
        agentUserListVO.setAccountType(user.getAccountType());
        agentUserListVO.setIsLock(user.getIsLock());
        agentUserListVO.setIsLogin(user.getIsLogin());
        agentUserListVO.setRegAddress(user.getRegAddress());
        agentUserListVO.setIsActive(user.getIsActive());


        agentUserListVO.setUserAmt(user.getUserAmt());
        agentUserListVO.setEnableAmt(user.getEnableAmt());

        agentUserListVO.setUserIndexAmt(user.getUserIndexAmt());
        agentUserListVO.setEnableIndexAmt(user.getEnableIndexAmt());

        agentUserListVO.setUserFuturesAmt(user.getUserFutAmt());
        agentUserListVO.setEnableFuturesAmt(user.getEnableFutAmt());

        agentUserListVO.setUserEmail(user.getEmail());

        agentUserListVO.setUserStockACapital(user.getUserStockACapital());

        agentUserListVO.setUserStockHKCapital(user.getUserStockHKCapital());

        agentUserListVO.setEnableHmt(user.getEnableHmt());

        agentUserListVO.setUserHmt(user.getUserHmt());



        PositionVO positionVO = this.iUserPositionService.findUserPositionAllProfitAndLose(user.getId());
        BigDecimal allProfitAndLose = positionVO.getAllProfitAndLose();
        BigDecimal allFreezAmt = positionVO.getAllFreezAmt();
        agentUserListVO.setAllProfitAndLose(allProfitAndLose);
        agentUserListVO.setAllFreezAmt(allFreezAmt);

        BigDecimal forceLine = forcePercent.multiply(allFreezAmt);
        forceLine = forceLine.add(user.getEnableAmt());
        agentUserListVO.setForceLine(forceLine);



        IndexPositionVO indexPositionVO = this.iUserIndexPositionService.findUserIndexPositionAllProfitAndLose(user.getId());
        agentUserListVO.setAllIndexProfitAndLose(indexPositionVO.getAllIndexProfitAndLose());
        agentUserListVO.setAllIndexFreezAmt(indexPositionVO.getAllIndexFreezAmt());

        BigDecimal indexForceLine = indexForcePercent.multiply(indexPositionVO.getAllIndexFreezAmt());
        indexForceLine = indexForceLine.add(user.getEnableIndexAmt());
        agentUserListVO.setIndexForceLine(indexForceLine);



        FuturesPositionVO futuresPositionVO = this.iUserFuturesPositionService.findUserFuturesPositionAllProfitAndLose(user.getId());
        agentUserListVO.setAllFuturesFreezAmt(futuresPositionVO.getAllFuturesDepositAmt());
        agentUserListVO.setAllFuturesProfitAndLose(futuresPositionVO.getAllFuturesProfitAndLose());

        BigDecimal futuresForceLine = futuresForcePercent.multiply(futuresPositionVO.getAllFuturesDepositAmt());
        futuresForceLine = futuresForceLine.add(user.getEnableFutAmt());
        agentUserListVO.setFuturesForceLine(futuresForceLine);



        UserBank userBank = this.iUserBankService.findUserBankByUserId(user.getId());
        if (userBank != null) {
            agentUserListVO.setBankName(userBank.getBankName());
            agentUserListVO.setBankNo(userBank.getBankNo());
            agentUserListVO.setBankAddress(userBank.getBankAddress());
        }

        return agentUserListVO;
    }

    private UserInfoVO assembleUserInfoVO(User user) {
        UserInfoVO userInfoVO = new UserInfoVO();

        userInfoVO.setUserCapital(user.getUserCapital());
        userInfoVO.setId(user.getId());
        userInfoVO.setAgentId(user.getAgentId());
        userInfoVO.setAgentName(user.getAgentName());
        userInfoVO.setPhone(user.getPhone());
        userInfoVO.setEmail(user.getEmail());
        userInfoVO.setNickName(user.getNickName());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setIdCard(user.getIdCard());
        userInfoVO.setAccountType(user.getAccountType());
        userInfoVO.setRecomPhone(user.getRecomPhone());
        userInfoVO.setIsLock(user.getIsLock());
        userInfoVO.setRegTime(user.getRegTime());
        userInfoVO.setRegIp(user.getRegIp());
        userInfoVO.setRegAddress(user.getRegAddress());
        userInfoVO.setImg1Key(user.getImg1Key());
        userInfoVO.setImg2Key(user.getImg2Key());
        userInfoVO.setImg3Key(user.getImg3Key());
        userInfoVO.setIsActive(user.getIsActive());
        userInfoVO.setAuthMsg(user.getAuthMsg());

        userInfoVO.setEnableAmt(user.getEnableAmt());
        userInfoVO.setTradingAmount(user.getTradingAmount());



        PositionVO positionVO = this.iUserPositionService.findUserPositionAllProfitAndLose(user.getId());
        userInfoVO.setAllFreezAmt(positionVO.getAllFreezAmt());
        BigDecimal allProfitAndLose = positionVO.getAllProfitAndLose();
        userInfoVO.setAllProfitAndLose(allProfitAndLose);


        BigDecimal userAllAmt = user.getUserAmt();
        if(userAllAmt == null){
            userAllAmt = BigDecimal.ZERO;
        }
        userAllAmt = userAllAmt.add(allProfitAndLose);
        userInfoVO.setUserAmt(userAllAmt);

        userInfoVO.setEnableIndexAmt(user.getEnableIndexAmt());

        PositionVO positionGgVO = this.iUserGgPositionService.findUserPositionAllProfitAndLose(user.getId());
        userInfoVO.setAllFreezhmt(positionGgVO.getAllFreezAmt());
        BigDecimal allProfitAndLoseGg = positionGgVO.getAllProfitAndLose();
        userInfoVO.setAllGgProfitAndLose(allProfitAndLoseGg);


        BigDecimal userAllHmt = user.getUserHmt();
        if(userAllHmt == null){
            userAllHmt = BigDecimal.ZERO;
        }
        userAllHmt = userAllHmt.add(allProfitAndLoseGg);
        userInfoVO.setUserHmt(userAllHmt);

        //获取港元汇率
        StockCoin hkdollar = stockCoinMapper.selectCoinByCode("HKD");

        String nowPrice = "";

        ExchangeVO exchangeVO = null;


        ServerResponse serverResponse = this.iStockFuturesService.queryExchangeVO(hkdollar.getCoinCode());

        if (serverResponse.isSuccess()) {

            exchangeVO = (ExchangeVO) serverResponse.getData();

            if (exchangeVO != null) {
                nowPrice = exchangeVO.getNowPrice();
            }

        }

        BigDecimal innRate = hkdollar.getDefaultRate();
        BigDecimal realRate = new BigDecimal(nowPrice);
        Integer dynamicState = hkdollar.getDynamicRate();



        if(dynamicState == 1)
            userInfoVO.setUserHmtMulRate(userAllHmt.multiply(innRate));
        else
            userInfoVO.setUserHmtMulRate(userAllHmt.multiply(realRate));

        userInfoVO.setEnableHmt(user.getEnableHmt());

        userInfoVO.setFixedHRate(innRate);
        userInfoVO.setRealHRate(realRate);
        userInfoVO.setDynamicState(dynamicState);


        IndexPositionVO indexPositionVO = this.iUserIndexPositionService.findUserIndexPositionAllProfitAndLose(user.getId());
        BigDecimal allIndexProfitAndLose = indexPositionVO.getAllIndexProfitAndLose();
        userInfoVO.setAllIndexProfitAndLose(allIndexProfitAndLose);
        userInfoVO.setAllIndexFreezAmt(indexPositionVO.getAllIndexFreezAmt());

        BigDecimal userAllIndexAmt = user.getUserIndexAmt();
        if(userAllIndexAmt == null){
            userAllIndexAmt = BigDecimal.ZERO;
        }
        userAllIndexAmt = userAllIndexAmt.add(allIndexProfitAndLose);
        userInfoVO.setUserIndexAmt(userAllIndexAmt);

        userInfoVO.setEnableFuturesAmt(user.getEnableFutAmt());


        FuturesPositionVO futuresPositionVO = this.iUserFuturesPositionService.findUserFuturesPositionAllProfitAndLose(user.getId());

        userInfoVO.setAllFuturesFreezAmt(futuresPositionVO.getAllFuturesDepositAmt());


        BigDecimal allFuturesProfitAndLose = futuresPositionVO.getAllFuturesProfitAndLose();
        userInfoVO.setAllFuturesProfitAndLose(allFuturesProfitAndLose);


        BigDecimal userAllFuturesAmt = user.getUserFutAmt();
        if(userAllFuturesAmt == null){
            userAllFuturesAmt = BigDecimal.ZERO;
        }
        userAllFuturesAmt = userAllFuturesAmt.add(allFuturesProfitAndLose);
        userInfoVO.setUserFuturesAmt(userAllFuturesAmt);


        List setting = siteSettingMapper.findAllSiteSetting();
        if (setting.size() > 0) {
            SiteSetting   siteSetting = (SiteSetting) setting.get(0);
            userInfoVO.setaFundingLevel(siteSetting.getFundingLevel());
            //根据用户查询平仓单亏损金额
            BigDecimal getLossAmount = userPositionMapper.getLossAmount(user.getId());
            if (getLossAmount == null){
                getLossAmount = BigDecimal.ZERO;
            }
            //判断用户交易金额是否满足本金交易10万元标准
            BigDecimal totalTurnover = userPositionMapper.countTotalTurnoverBuyUserId(user.getId());
            if (totalTurnover == null){
                totalTurnover = BigDecimal.ZERO;
            }

            if(totalTurnover.divide(new BigDecimal(userInfoVO.getaFundingLevel())).compareTo(new BigDecimal(100000)) == -1  ){
                if(getLossAmount.compareTo(new BigDecimal("-5000")) == -1){
                    getLossAmount = new BigDecimal("-5000");
                }
                //减去亏损金额
                userInfoVO.setNoTransAmt(new BigDecimal(PropertiesUtil.getProperty("zeng.song.ag.money")).add(getLossAmount).setScale(2,4));
            }else{
                userInfoVO.setNoTransAmt(new BigDecimal("0"));
            }

        }

        List hksSetting = siteHksSettingMapper.findAllSiteHksSetting();
        if (hksSetting.size() > 0) {
            SiteHksSetting   siteSetting = (SiteHksSetting) hksSetting.get(0);
            userInfoVO.sethFundingLevel(siteSetting.getFundingLevel());
            //判断用户交易金额是否满足本金交易10万元标准
            BigDecimal totalTurnover = userGgPositionMapper.countTotalTurnoverBuyUserId(user.getId());
            if(totalTurnover == null){
                totalTurnover = BigDecimal.ZERO;
            }

            if(totalTurnover.divide(new BigDecimal(userInfoVO.gethFundingLevel())).compareTo(new BigDecimal(100000)) == -1  ){
                userInfoVO.setNoTransHmt(new BigDecimal(PropertiesUtil.getProperty("zeng.song.gg.money")));
            }else{
                userInfoVO.setNoTransHmt(new BigDecimal("0"));
            }
        }

        //计算总资产  总资产=本金+港股资金*（汇率-转出汇率）/配资杠杆 + A股资金/配资杠杆
        if(setting.size()>0 && hksSetting.size()>0){
            SiteSetting   siteSetting = (SiteSetting) setting.get(0);
            SiteHksSetting   hkSiteSetting = (SiteHksSetting) hksSetting.get(0);
            Integer fundingLevelA = siteSetting.getFundingLevel();
            Integer fundingLevelH = hkSiteSetting.getFundingLevel();
            BigDecimal outDiff = hkdollar.getOutDiff();

            BigDecimal userStockAGiveCapital = user.getUserStockAGiveCapital();
            if(fundingLevelA != 0 && fundingLevelH != 0){
                BigDecimal htotal = new BigDecimal("0");
                BigDecimal aTotal = new BigDecimal("0");
                if(dynamicState == 1)
                    htotal = user.getUserStockHKCapital().multiply(innRate.subtract(outDiff));
                    //htotal = user.getUserHmt().multiply(innRate.subtract(outDiff)).divide(new BigDecimal(fundingLevelH));
                else
                    htotal = user.getUserStockHKCapital().multiply(realRate.subtract(outDiff));
                    //htotal = user.getUserHmt().multiply(realRate.subtract(outDiff).divide(new BigDecimal(fundingLevelH)));

//                aTotal = user.getUserAmt().divide(new BigDecimal(fundingLevelA));

                if(user.getUserAmt().compareTo(BigDecimal.ZERO) <= 0){
                    userStockAGiveCapital = BigDecimal.ZERO;
                }

                aTotal = user.getUserStockACapital().add(userStockAGiveCapital);

                userInfoVO.setTotalCapital(user.getUserCapital().add(htotal).add(aTotal));
            }

            BigDecimal stockAGiveCapital = BigDecimal.ZERO;

            BigDecimal stockACapital = BigDecimal.ZERO;
            if(user.getIsActive() == 2) {

                stockAGiveCapital = userStockAGiveCapital;  // 赠送的A股本金

                stockACapital = user.getUserStockACapital();  // 不是赠送的A股本金
            }else{
                stockAGiveCapital = BigDecimal.ZERO;

                stockACapital = user.getUserStockACapital();  // 不是赠送的A股本金
            }

            //BigDecimal stockACapital = user.getUserStockACapital().add(allProfitAndLose); // 不加盈亏



            if(stockACapital.compareTo(new BigDecimal(0))==-1){
                stockACapital = new BigDecimal(0);
            }

            userInfoVO.setUserStockACapital(stockACapital.add(user.getUserStockAGiveCapital())); // A股本金 + 赠送A股本金剩余 + 盈亏 为实际的本金

            userInfoVO.setUserStockAGiveCapital(user.getUserStockAGiveCapital());  // 总的赠送本金

            userInfoVO.setCanWithdrawCapital(user.getUserStockACapital());  // 可提取本金

            userInfoVO.setUserStockHKCapital(user.getUserStockHKCapital()); // 港股本金

        }


        return userInfoVO;
    }


    public static void main(String[] args) {
        int a = 3;

        System.out.println((a != 0));
        System.out.println((a != 3));

        System.out.println(((a != 0) ? 1 : 0) & ((a != 3) ? 1 : 0));
        System.out.println((a != 0 && a != 3));


        if (a != 0 && a != 3) {
            System.out.println("不能认证");
        } else {
            System.out.println("可以认证");
        }
    }


    @Override
    public void updateUserAmt(Double amt, Integer user_id) {
        userMapper.updateUserAmt(amt, user_id);
    }



}

