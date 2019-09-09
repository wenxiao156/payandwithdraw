package com.cmit.payandwithdraw.web;

import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.service.AccountService;
import com.cmit.payandwithdraw.util.TokenUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RestController
@RequestMapping(value="/saler")
public class TokenController {
//    private static final int SALER_NUM = 100;
    @Autowired
    AccountService  accountService;
    ArrayList<Account>  accounts = new ArrayList<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(10);

    @ApiOperation(value="获取token", notes="用户争抢10个token，返回成功的用户id")
    @RequestMapping(value="/getToken",method=RequestMethod.GET)
    public String getToken() {
        List<Account> accountList = accountService.findAll();
        for(int i = 0; i < accountList.size(); i++) {
            accountService.getToken(accountList.get(i).getSalerId());
        }
        StringBuilder salerIds = new StringBuilder("");
        for(String salerId : accountService.getSalerToken().keySet()) {
            accounts.add(accountService.selectById(salerId));
            salerIds.append(salerId);
        }
        return salerIds.toString();
    }

}
