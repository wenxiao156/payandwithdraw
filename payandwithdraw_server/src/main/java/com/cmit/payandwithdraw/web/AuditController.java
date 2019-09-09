package com.cmit.payandwithdraw.web;

import com.cmit.payandwithdraw.service.AuditService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/audit")
public class AuditController {
    @Autowired
    private AuditService auditService;

    @ApiOperation(value = "稽核", notes = "汇总金额值，与对应账号金额值进行比对")
    @RequestMapping(value = "/startAudit", method = RequestMethod.POST)
    public void getCsv() {
        auditService.audit();
    }

}
