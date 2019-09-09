package com.cmit.payandwithdraw.service;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.*;
import com.cmit.payandwithdraw.util.CsvExportUtil;
import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PayService {
    @Autowired
    private PayDAO payDAO;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private PayReconciliationService payReconciliationService;

    @Autowired
    private WithdrawReconciliationService withdrawReconciliationService;

    ReentrantLock lock = new ReentrantLock();

    Map<String, List<Float>> cache = new HashMap<>();

    public Pay selectById(String id) {
        return payDAO.selectById(id);
    }

    public List<Pay> findAll() {
        return payDAO.findAll();
    }

    public int addPay(Pay pay) {
        return payDAO.addPay(pay);
    }

    public void deletePay(String id) {
        payDAO.deletePay(id);
    }

    public void updatePay(Pay pay) {
        payDAO.updatePay(pay);
    }

    /**
     * 将成功的支付订单的金额保存在feeList缓存中
     * 根据支付订单生成响应，构造支付系统上的Pay数据
     */
    public Pay add(JSONObject jsonParam, String responseMsg, ResponseCode responseCode) {
        if (responseCode == ResponseCode.SUCCESS) {
            String salerId = jsonParam.get("salerId").toString();
            List<Float> feeList;
            if (cache.containsKey(salerId)) {
                feeList = cache.get(salerId);
            } else {
                feeList = new ArrayList<Float>();
            }
            feeList.add(Float.valueOf(jsonParam.get("totalFee").toString()));
            cache.put(salerId, feeList);
        }
        Pay pay = new Pay();
        pay.setRequestId(jsonParam.get("requestId").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            pay.setRequestTime(df.parse(jsonParam.get("requestTime").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        pay.setSalerId(jsonParam.get("salerId").toString());
        pay.setTotalFee(Float.valueOf(jsonParam.get("totalFee").toString()));
        pay.setResponseTime(new Date());
        pay.setResponseId(pay.getResponseTime().toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + (int) ((Math.random() * 9 + 1) * 100000));
        pay.setResponseCode(responseCode);
        pay.setResponseMsg(responseMsg);
        addPay(pay);
        return pay;
    }

    /**
     * 定时计算缓存中的数据，修改账户的金额
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void updateAccount() {
        System.out.println("-----------定时任务执行---------" + LocalDateTime.now() + "----" + cache.size());
        lock.lock();
        for (String salerId : cache.keySet()) {
            Account account = accountService.selectById(salerId);
            float origin = account.getMoney();
            List<Float> feeList = cache.get(salerId);
            float change = 0;
            for (float fee : feeList) {
                change += fee;
            }
            account.setMoney(origin + change);
            accountService.updateAccount(account);
        }
        cache.clear();
        lock.unlock();
    }

    /**
     * 将数据库中的数据构造成map，方便进行字符串的拼接
     */
    private List<Map<String, Object>> changeToMapList() {
        // 查询需要导出的数据
        List<Pay> payList = findAll();
        // 构造导出数据
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<String, Object> map = null;
        if (payList.size() > 0) {
            for (Pay pay : payList) {
                //失败的不导出
                if (pay.getResponseCode() == ResponseCode.FAIL) {
                    continue;
                }
                map = new HashMap<>();
                map.put("requestId", pay.getRequestId());
                map.put("requestTime", pay.getRequestTime());
                map.put("salerId", pay.getSalerId());
                map.put("totalFee", pay.getTotalFee());
                map.put("responseId", pay.getResponseId());
                map.put("responseTime", pay.getResponseTime());
                map.put("responseCode", pay.getResponseCode());
                map.put("responseMsg", pay.getResponseMsg());
                datas.add(map);
            }
        }
        return datas;
    }

    /**
     * 给定标题和key值构造csv文件的内容
     */
    public StringBuffer getStringBuffer() {
        List<Map<String, Object>> payMaps = changeToMapList();
        // 构造导出数据结构
        String payTitles = "request_id,request_time,saler_id,total_fee,response_id,response_time,response_code,response_msg";  // 设置表头
        String payKeys = "requestId,requestTime,salerId,totalFee,responseId,responseTime,responseCode,responseMsg";  // 设置每列字段
        return CsvExportUtil.getStringBuffer(payMaps, payTitles, payKeys);
    }

    /**
     * 获取项目的templates文件夹的文件路径
     * @param fName 文件名称
     */
    private String getPath(String fName) {
        //项目路径
        String path = System.getProperty("user.dir");
        return path + "/src/main/resources/templates/" + fName;
    }

    /**
     * 生成成功的支付和提款的csv文件
     */
    public File generateCsv() {
        File file = new File(getPath("payAndWithdraw.csv"));
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            StringBuffer pay = getStringBuffer();
            StringBuffer withdraw = withdrawService.getStringBuffer();
            String content = pay.append(withdraw).toString();
            for (int i = 0; i < content.length(); i++) {
                fos.write((int) content.charAt(i));
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 处理商户系统和支付系统的csv文件
     */
    public void reconciliation() {
        File client = new File(getPath("clientPayAndWithdraw.csv"));
        File server = new File(getPath("payAndWithdraw.csv"));
        doFile(client, true);
        doFile(server, false);
    }

    /**
     * 处理商户系统和支付系统的csv文件
     * 对平记录记为为F000，支付系统比商户系统多的记录记为F113
     * 商户系统比支付系统多的记录记为F114，支付系统和商户系统都有但金额不相同的记录记为F115
     * @param file 商户系统或者支付系统导出的成功的订单的csv文件
     */
    private void doFile(File file, Boolean isClient) {
        CsvReader csvReader = new CsvReader();
        csvReader.setContainsHeader(true);
        CsvContainer csv = null;
        try {
            csv = csvReader.read(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Boolean isPay = true;
        for (CsvRow row : csv.getRows()) {
            if ("request_id".equals(row.getField(0))) {
                isPay = false;
                continue;
            }
            //商户系统的支付订单
            if (isPay && isClient) {
                PayReconciliation selectedPay = payReconciliationService.selectById(row.getField(4));
                if (selectedPay == null) {
                    createPayReconciliation(row, DiffType.F114);
                }
                //商户系统的提款订单
            } else if (isClient) {
                WithdrawReconciliation selectedWithdraw = withdrawReconciliationService.selectById(row.getField(4));
                if (selectedWithdraw == null) {
                    createWithdrawReconciliation(row, DiffType.F114);
                }
                //支付系统的支付订单
            } else if (isPay && !isClient) {
                PayReconciliation selectedPay = payReconciliationService.selectById(row.getField(4));
                if (selectedPay != null) {
                    if (selectedPay.getTotalFee() == Float.valueOf(row.getField(3))) {
                        selectedPay.setDiffType(DiffType.F000);
                    } else {
                        selectedPay.setDiffType(DiffType.F115);
                    }
                    payReconciliationService.updatePay(selectedPay);
                } else {
                    createPayReconciliation(row, DiffType.F113);
                }
                //支付系统的提款订单
            } else if (!isClient) {
                WithdrawReconciliation selectedWithdraw = withdrawReconciliationService.selectById(row.getField(4));
                if (selectedWithdraw != null) {
                    if (selectedWithdraw.getWithdrawMoney() == Float.valueOf(row.getField(3))) {
                        selectedWithdraw.setDiffType(DiffType.F000);
                    } else {
                        selectedWithdraw.setDiffType(DiffType.F115);
                    }
                    withdrawReconciliationService.updateWithdraw(selectedWithdraw);
                } else {
                    createWithdrawReconciliation(row, DiffType.F113);
                }
            }
        }
    }

    /**
     * 创建新的PayReconciliation
     * @param row      csv文件每一行的数据
     * @param diffType PayReconciliation的对账结果
     */
    private void createPayReconciliation(CsvRow row, DiffType diffType) {
        PayReconciliation pay = new PayReconciliation();
        System.out.println("requestId" + row.getField(0));
        pay.setRequestId(row.getField(0));
        pay.setResponseId(row.getField(4));
        pay.setTotalFee(Float.valueOf(row.getField(3)));
        pay.setDiffType(diffType);
        payReconciliationService.addPay(pay);
    }

    /**
     * 创建新的WithdrawReconciliation
     * @param row      csv文件每一行的数据
     * @param diffType WithdrawReconciliation的对账结果
     */
    private void createWithdrawReconciliation(CsvRow row, DiffType diffType) {
        WithdrawReconciliation withdraw = new WithdrawReconciliation();
        withdraw.setRequestId(row.getField(0));
        withdraw.setResponseId(row.getField(4));
        withdraw.setWithdrawMoney(Float.valueOf(row.getField(3)));
        withdraw.setDiffType(diffType);
        withdrawReconciliationService.addWithdraw(withdraw);
    }
}
