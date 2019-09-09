package com.cmit.payandwithdraw.service;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.*;
import com.cmit.payandwithdraw.util.CsvExportUtil;
import com.cmit.payandwithdraw.util.SendRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ClientWithdrawService {
    @Autowired
    private ClientWithdrawDAO withdrawDAO;

    public ClientWithdraw selectById(String id) {
        return withdrawDAO.selectById(id);
    }

    public List<ClientWithdraw> findAll() {
        return withdrawDAO.findAll();
    }

    public int addWithdraw(ClientWithdraw Withdraw) {
        return withdrawDAO.addWithdraw(Withdraw);
    }

    public void deleteWithdraw(String id) {
        withdrawDAO.deleteWithdraw(id);
    }

    public void updateWithdraw(ClientWithdraw Withdraw) {
        withdrawDAO.updateWithdraw(Withdraw);
    }

    /**
     * 异步方法，多线程生成提款订单发送到支付系统，并处理支付系统返回的响应
     * @param salerId
     * @return
     */
    @Async(value = "withdraw")
    public String withdrawRequest(int salerId) {
        ClientWithdraw withdraw = new ClientWithdraw();
        withdraw.setRequestTime(new Date());
        withdraw.setRequestId(withdraw.getRequestTime().toString().replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + (int) ((Math.random() * 9 + 1) * 100000));
        withdraw.setSalerId("saler" + salerId);
        withdraw.setWithdrawMoney((int) (Math.random() * 100) + 1);
        addWithdraw(withdraw);
        String suffix = "/withdraw/addWithdraw";
        ResponseEntity<String> response = SendRequestUtil.toSend(suffix, MediaType.APPLICATION_JSON_UTF8, withdraw, HttpMethod.POST);
        JSONObject object = JSONObject.parseObject(response.getBody());
        ClientWithdraw updateWithdraw = selectById(object.get("requestId").toString());
        updateWithdraw.setResponseId(object.get("responseId").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            updateWithdraw.setResponseTime(df.parse(object.get("responseTime").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateWithdraw.setResponseCode(ResponseCode.returnCode(object.get("responseCode").toString()));
        updateWithdraw.setResponseMsg(object.get("responseMsg").toString());
        withdrawDAO.updateWithdraw(updateWithdraw);
        return response.getBody();
    }

    /**
     * 将数据库中的数据构造成map，方便进行字符串的拼接
     */
    private List<Map<String, Object>> changeToMapList() {
        // 查询需要导出的数据
        List<ClientWithdraw> withdrawList = findAll();
        // 构造导出数据
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<String, Object> map = null;
        if (withdrawList.size() > 0) {
            for (ClientWithdraw withdraw : withdrawList) {
                //失败的不导出
                if (withdraw.getResponseCode() == ResponseCode.FAIL || withdraw.getResponseCode() == null) {
                    continue;
                }
                map = new HashMap<>();
                map.put("requestId", withdraw.getRequestId());
                map.put("requestTime", withdraw.getRequestTime());
                map.put("salerId", withdraw.getSalerId());
                map.put("withdrawMoney", withdraw.getWithdrawMoney());
                map.put("responseId", withdraw.getResponseId());
                map.put("responseTime", withdraw.getResponseTime());
                map.put("responseCode", withdraw.getResponseCode());
                map.put("responseMsg", withdraw.getResponseMsg());
                datas.add(map);
            }
        }
        return datas;
    }

    /**
     * 给定标题和key值构造csv文件的内容
     */
    public StringBuffer getStringBuffer(){
        List<Map<String, Object>> withdrawMaps = changeToMapList();
        String withdrawTitles = "request_id,request_time,saler_id,withdraw_money,response_id,response_time,response_code,response_msg";  // 设置表头
        String withdrawKeys = "requestId,requestTime,salerId,withdrawMoney,responseId,responseTime,responseCode,responseMsg";  // 设置每列字段
        return CsvExportUtil.getStringBuffer(withdrawMaps, withdrawTitles,withdrawKeys);
    }
}
