package com.cmit.payandwithdraw.web;

import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.service.AccountService;
import com.cmit.payandwithdraw.service.ClientWithdrawService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/clientWithdraw")
public class ClientWithdrawController {
    @Autowired
    ClientWithdrawService clientWithdrawService;
    @Autowired
    AccountService accountService;

    @ApiOperation(value="新增提款订单", notes="多线程发起提款请求")
    @RequestMapping(value="/addWithdraw",method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public void addPay() {
        List<Account> accountList = accountService.findAll();
        int size = accountList.size();
        int[] salerIds = new int[size];
        for (int i = 0; i < size; i++) {
            salerIds[i] = i + 1;
        }
        int count = size - 1;
        int random;
        for (int i = 0; i < size; i++) {
            if(i == size -1) {
                random = 0;
            } else {
                random = (int) (Math.random() * (count - 1)) + 1;
            }
            int salerId = salerIds[random];
            salerIds[random] = salerIds[count];
            count--;
            clientWithdrawService.withdrawRequest(salerId);
        }
    }
}
