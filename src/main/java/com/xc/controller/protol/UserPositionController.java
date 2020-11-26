 package com.xc.controller.protol;

 import com.xc.common.ServerResponse;
 import com.xc.service.IUserPositionService;
 import javax.servlet.http.HttpServletRequest;

 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.ResponseBody;

 @Controller
 @RequestMapping({"/user/position/"})
 public class UserPositionController {
     private static final Logger log = LoggerFactory.getLogger(UserPositionController.class);

     @Autowired
     IUserPositionService iUserPositionService;

     //查询所有融资平仓/持仓信息
     @RequestMapping({"list.do"})
     @ResponseBody
     public ServerResponse list(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "state", required = false) Integer state, @RequestParam(value = "stockCode", required = false) String stockCode, @RequestParam(value = "stockSpell", required = false) String stockSpell) {
         return this.iUserPositionService.findMyPositionByCodeAndSpell(stockCode, stockSpell, state, request, pageNum, pageSize);
     }

     //根据股票代码查询用户最早入仓股票
     @RequestMapping({"findUserPositionByCode.do"})
     @ResponseBody
     public ServerResponse findUserPositionByCode(HttpServletRequest request, @RequestParam(value = "stockCode", required = false) String stockCode) {
         return this.iUserPositionService.findUserPositionByCode(request, stockCode);
     }
 }

