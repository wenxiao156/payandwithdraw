package com.cmit.payandwithdraw.web;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.domain.Pay;
import com.cmit.payandwithdraw.domain.ResponseCode;
import com.cmit.payandwithdraw.domain.Withdraw;
import com.cmit.payandwithdraw.service.AccountService;
import com.cmit.payandwithdraw.service.PayService;
import com.cmit.payandwithdraw.service.WithdrawService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/withdraw")
//@CacheConfig(cacheNames = "pay")
public class WithdrawController {
    private static final int REQUEST_ID_LENGTH = 20;
    @Autowired
    WithdrawService withdrawService;
    @Autowired
    AccountService accountService;

    /**
     * 校验商户系统传过来的提款订单，生成responseCode和responseMsg，返回生成的withdraw
     */
//    @Cacheable
    @ApiOperation(value = "新增提款订单", notes = "处理提款请求")
    @RequestMapping(value = "/addWithdraw", method = RequestMethod.POST)
    public Withdraw addWithdraw(@RequestBody JSONObject jsonParam) {
        JSONObject result = new JSONObject();
        String responseMsg = "OK";
        ResponseCode responseCode = ResponseCode.SUCCESS;
        List<Account> accountList = accountService.findAll();
        List<String> salersIdList = new ArrayList<>();
        for (Account account : accountList) {
            salersIdList.add(account.getSalerId());
        }
        //校验
        if (jsonParam.get("requestId").toString().length() != REQUEST_ID_LENGTH) {
            responseMsg = "请求Id不合法！";
            responseCode = ResponseCode.FAIL;
        }
        if (!salersIdList.contains(jsonParam.get("salerId"))) {
            responseMsg = "用户不存在";
            responseCode = ResponseCode.FAIL;
        }
        float withdrawMoney = Float.valueOf(jsonParam.get("withdrawMoney").toString());
        if (withdrawMoney < 0) {
            responseMsg = "提款金额小于0";
            responseCode = ResponseCode.FAIL;
        }
        if(withdrawMoney > accountService.selectById(jsonParam.get("salerId").toString()).getMoney()) {
            responseMsg = "账户余额不足";
            responseCode = ResponseCode.FAIL;
        }
        return withdrawService.add(jsonParam, responseMsg, responseCode);
    }
}
