package com.cmit.payandwithdraw.service;

import com.alibaba.fastjson.JSONObject;
import com.cmit.payandwithdraw.domain.Account;
import com.cmit.payandwithdraw.domain.ResponseCode;
import com.cmit.payandwithdraw.domain.Withdraw;
import com.cmit.payandwithdraw.domain.WithdrawDAO;
import com.cmit.payandwithdraw.util.CsvExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WithdrawService {
    @Autowired
    private WithdrawDAO withdrawDAO;
    @Autowired
    private AccountService accountService;

    public Withdraw selectById(String id){
        return withdrawDAO.selectById(id);
    }

    public List<Withdraw> findAll(){
        return withdrawDAO.findAll();
    }

    public int addWithdraw(Withdraw Withdraw){
        return withdrawDAO.addWithdraw(Withdraw);
    }

    public void  deleteWithdraw(String id){
        withdrawDAO.deleteWithdraw(id);
    }

    public void updateWithdraw(Withdraw Withdraw){
        withdrawDAO.updateWithdraw(Withdraw);
    }

    /**
     * 根据提款订单生成响应，构造支付系统上的Withdraw数据
     */
    public Withdraw add(JSONObject jsonParam,String responseMsg,ResponseCode responseCode) {
        Withdraw withdraw = new Withdraw();
        withdraw.setRequestId(jsonParam.get("requestId").toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            withdraw.setRequestTime(df.parse(jsonParam.get("requestTime").toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String salerId = jsonParam.get("salerId").toString();
        withdraw.setSalerId(salerId);
        float withdrawMoney = Float.valueOf(jsonParam.get("withdrawMoney").toString());
        withdraw.setWithdrawMoney(withdrawMoney);
        withdraw.setResponseTime(new Date());
        withdraw.setResponseId(withdraw.getResponseTime().toString().replaceAll("-","").replaceAll(":","").replaceAll(" ","") + (int)((Math.random()*9+1)*100000));
        withdraw.setResponseCode(responseCode);
        withdraw.setResponseMsg(responseMsg);
        addWithdraw(withdraw);
        if(responseCode == ResponseCode.SUCCESS) {
            updateAccount(salerId,withdrawMoney);
        }
        return withdraw;
    }

    /**
     * 根据提款金额从账户上扣取相应的金额
     */
    private void updateAccount(String salerId, float withdrawMoney) {
        Account account = accountService.selectById(salerId);
        account.setMoney(account.getMoney() - withdrawMoney);
        accountService.updateAccount(account);
    }

    /**
     * 将数据库中的数据构造成map，方便进行字符串的拼接
     */
    private List<Map<String, Object>> changeToMapList() {
        // 查询需要导出的数据
        List<Withdraw> withdrawList = findAll();
        // 构造导出数据
        List<Map<String, Object>> datas = new ArrayList<>();
        Map<String, Object> map = null;
        if (withdrawList.size() > 0) {
            for (Withdraw withdraw : withdrawList) {
                //失败的不导出
                if (withdraw.getResponseCode() == ResponseCode.FAIL) {
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
