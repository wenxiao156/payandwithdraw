package com.cmit.payandwithdraw.service;

import com.cmit.payandwithdraw.domain.AccountDAO;
import com.cmit.payandwithdraw.domain.Audit;
import com.cmit.payandwithdraw.domain.AuditDAO;
import com.cmit.payandwithdraw.domain.ResponseCode;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {
    @Autowired
    private AuditDAO auditDAO;
    @Autowired
    private AccountDAO accountDAO;

    public Audit selectById(String id) {
        return auditDAO.selectById(id);
    }

    public List<Audit> findAll() {
        return auditDAO.findAll();
    }

    public int addAudit(Audit audit) {
        return auditDAO.addAudit(audit);
    }

    public void deleteAudit(String salerId) {
        auditDAO.deleteAudit(salerId);
    }

    public void updateAudit(Audit audit) {
        auditDAO.updateAudit(audit);
    }

    /**
     * 获取项目的templates文件夹的文件路径
     *
     * @param fName 文件名称
     */
    private String getPath(String fName) {
        //项目路径
        String path = System.getProperty("user.dir");
        return path + "/src/main/resources/templates/" + fName;
    }

    public void audit() {
        File client = new File(getPath("clientPayAndWithdraw.csv"));
        doFile(client);
    }

    /**
     * 处理商户系统导出的支付订单、提款订单csv文件，获得稽核金额
     */
    private void doFile(File file) {
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        CsvContainer csv = null;
        try {
            csv = csvReader.read(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Boolean isPay = true;
        Map<String, Float> salerAuditMoney = new HashMap<>();
        for (CsvRow row : csv.getRows()) {
            if ("request_id".equals(row.getField(0))) {
                isPay = false;
                continue;
            }
            String salerId = row.getField(2);
            float money = Float.valueOf(row.getField(3));
            if (isPay) {
                if (salerAuditMoney.containsKey(salerId)) {
                    salerAuditMoney.put(salerId, salerAuditMoney.get(salerId) + money);
                } else {
                    salerAuditMoney.put(salerId, money);
                }
            } else {
                if (salerAuditMoney.containsKey(salerId)) {
                    salerAuditMoney.put(salerId, salerAuditMoney.get(salerId) - money);
                } else {
                    salerAuditMoney.put(salerId, money);
                }
            }
        }
        mapForAudit(salerAuditMoney);
    }

    /**
     * 根据key为salerId，value为auditMoney的map新增或者更新audit
     * @param salerAuditMoney
     */
    private void mapForAudit(Map<String, Float> salerAuditMoney) {
        for (String salerId : salerAuditMoney.keySet()) {
            Audit audit = selectById(salerId);
            if(audit == null) {
                audit = new Audit();
                audit.setBalance(accountDAO.selectById(salerId).getMoney());
                audit.setAuditMoney(salerAuditMoney.get(salerId));
                audit.setAuditResult(audit.getAuditMoney() == audit.getBalance() ? ResponseCode.SUCCESS : ResponseCode.FAIL);
                audit.setSalerId(salerId);
                addAudit(audit);
            } else {
                audit.setBalance(accountDAO.selectById(salerId).getMoney());
                audit.setAuditMoney(salerAuditMoney.get(salerId));
                audit.setAuditResult(audit.getAuditMoney() == audit.getBalance() ? ResponseCode.SUCCESS : ResponseCode.FAIL);
                updateAudit(audit);
            }
        }
    }
}
