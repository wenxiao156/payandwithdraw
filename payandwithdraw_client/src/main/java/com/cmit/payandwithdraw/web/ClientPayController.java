package com.cmit.payandwithdraw.web;

import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.service.AccountService;
import com.cmit.payandwithdraw.service.ClientPayService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/clientpay")
public class ClientPayController {
    @Autowired
    ClientPayService clientPayService;
    @Autowired
    AccountService accountService;

    @ApiOperation(value = "新增支付订单", notes = "多线程发起支付请求")
    @RequestMapping(value = "/addPay", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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
            clientPayService.payRequest(salerId);
        }
    }

    @ApiOperation(value = "导出csv文件", notes = "商户系统将成功的支付订单导出为csv文件发送到支付系统")
    @RequestMapping(value = "/exportCsv", method = RequestMethod.GET)
    public void exportCsv(HttpServletResponse response) {
        clientPayService.sendCsv();
    }
}
