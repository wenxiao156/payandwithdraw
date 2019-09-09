package com.cmit.payandwithdraw.web;

import com.cmit.payandwithdraw.service.PayService;
import com.cmit.payandwithdraw.service.WithdrawService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(value = "/reconciliation")
public class ReconciliationController {
    @Autowired
    PayService payService;
    @Autowired
    WithdrawService withdrawService;

    /**
     * 对账，导出支付系统支付和提款订单生成csv文件，接收商户系统传送过来的csv文件
     * 根据csv文件的数据进行对账处理
     */
    @ApiOperation(value = "接收商户系统的csv文件", notes = "处理csv文件")
    @RequestMapping(value = "/getCsv", method = RequestMethod.POST)
    public void getCsv(@RequestParam("file") MultipartFile multipartFile) {
        payService.generateCsv();
        try {
            String path = System.getProperty("user.dir");
            File clientCsv = new File(path + "/src/main/resources/templates/" + multipartFile.getOriginalFilename());
            clientCsv.createNewFile();
            multipartFile.transferTo(clientCsv);
            payService.reconciliation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
