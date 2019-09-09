package com.cmit.payandwithdraw.service;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.ClientPay;
import com.cmit.payandwithdraw.domain.ClientPayDAO;
import com.cmit.payandwithdraw.domain.ResponseCode;
import com.cmit.payandwithdraw.util.CsvExportUtil;
import com.cmit.payandwithdraw.util.SendRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ClientPayService {
    @Autowired
    private ClientPayDAO payDAO;
    @Autowired
    private ClientWithdrawService withdrawService;

    public ClientPay selectById(String id) {
        return payDAO.selectById(id);
    }

    public List<ClientPay> findAll() {
        return payDAO.findAll();
    }

    public int addPay(ClientPay pay) {
        return payDAO.addPay(pay);
    }

    public void deletePay(String id) {
        payDAO.deletePay(id);
    }

    public void updatePay(ClientPay pay) {
        payDAO.updatePay(pay);
    }

    /**
     * 异步方法，多线程生成支付订单发送到支付系统，并处理支付系统返回的响应
     *
     * @param salerId
     * @return
     */
    @Async(value = "pay")
    public String payRequest(int salerId) {
        ClientPay pay = new ClientPay();
        pay.setRequestTime(new Date());
        pay.setRequestId(pay.getRequestTime().toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + (int) ((Math.random() * 9 + 1) * 100000));
        pay.setSalerId("saler" + salerId);
        pay.setTotalFee((int) (Math.random() * 100) + 1);
        addPay(pay);
        String suffix = "/pay/addPay";
        ResponseEntity<String> response = SendRequestUtil.toSend(suffix, MediaType.APPLICATION_JSON_UTF8, pay, HttpMethod.POST);
        JSONObject object = JSONObject.parseObject(response.getBody());
        ClientPay updatePay = selectById(object.get("requestId").toString());
        updatePay.setResponseId(object.get("responseId").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            updatePay.setResponseTime(df.parse(object.get("responseTime").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updatePay.setResponseCode(ResponseCode.returnCode(object.get("responseCode").toString()));
        updatePay.setResponseMsg(object.get("responseMsg").toString());
        payDAO.updatePay(updatePay);
        return response.getBody();
    }

    /**
     * 将数据库中的数据构造成map，方便进行字符串的拼接
     */
    private List<Map<String, Object>> changeToMapList() {
        // 查询需要导出的数据
        List<ClientPay> payList = findAll();
        // 构造导出数据
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<String, Object> map = null;
        if (payList.size() > 0) {
            for (ClientPay pay : payList) {
                //失败的与没有响应的不导出
                if (pay.getResponseCode() == ResponseCode.FAIL || pay.getResponseCode() == null) {
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
     * 获取项目的templates文件夹下的clientPayAndWithdraw.csv文件的绝对路径
     */
    private String getPath() {
        String fName = "clientPayAndWithdraw.csv";
        //项目路径
        String path = System.getProperty("user.dir");
        return path + "/src/main/resources/templates/" + fName;
    }

    /**
     * 将成功的支付订单和提款订单生成csv文件
     */
    private void exportCsv() {
        try {
            File file = new File(getPath());
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            StringBuffer pay = getStringBuffer();
            StringBuffer withdraw = withdrawService.getStringBuffer();
            String content = pay.append(withdraw).toString();
            for (int i = 0; i < content.length(); i++) {
                fos.write((int) content.charAt(i));
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将生成的csv文件发送到支付系统
     */
    public void sendCsv() {
        exportCsv();
        FileSystemResource fileSystemResource = new FileSystemResource(getPath());
        String suffix = "/reconciliation/getCsv";
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();
        form.add("file", fileSystemResource);
        ResponseEntity<String> response = SendRequestUtil.toSend(suffix, MediaType.MULTIPART_FORM_DATA, form, HttpMethod.POST);
        JSONObject object = JSONObject.parseObject(response.getBody());
    }
}
