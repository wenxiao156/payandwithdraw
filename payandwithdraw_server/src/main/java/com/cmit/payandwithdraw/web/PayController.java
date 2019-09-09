package com.cmit.payandwithdraw.web;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.domain.Pay;
import com.cmit.payandwithdraw.domain.ResponseCode;
import com.cmit.payandwithdraw.service.AccountService;
import com.cmit.payandwithdraw.service.PayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/pay")
//@CacheConfig(cacheNames = "pay")
public class PayController {
    private static final int REQUEST_ID_LENGTH = 20;
    @Autowired
    PayService payService;
    @Autowired
    AccountService accountService;

    /**
     * 校验商户系统传过来的支付订单，生成responseCode和responseMsg，返回生成的pay
     */
//    @Cacheable
    @ApiOperation(value = "新增支付订单", notes = "处理支付请求")
    @RequestMapping(value = "/addPay", method = RequestMethod.POST)
    public Pay addPay(@RequestBody JSONObject jsonParam) {
        JSONObject result = new JSONObject();
        String responseMsg = "OK";
        ResponseCode responseCode = ResponseCode.SUCCESS;
        List<Account> accountList = accountService.findAll();
        List<String> salersIdList = new ArrayList<>();
        for (Account account : accountList) {
            salersIdList.add(account.getSalerId());
        }
        //校验数据
        if (jsonParam.get("requestId").toString().length() != REQUEST_ID_LENGTH) {
            responseMsg = "请求Id不合法！";
            responseCode = ResponseCode.FAIL;
        }
        if (!salersIdList.contains(jsonParam.get("salerId"))) {
            responseMsg = "用户不存在";
            responseCode = ResponseCode.FAIL;
        }
        if (Float.valueOf(jsonParam.get("totalFee").toString()) < 0) {
            responseMsg = "支付金额小于0";
            responseCode = ResponseCode.FAIL;
        }
        return payService.add(jsonParam, responseMsg, responseCode);
    }
}
