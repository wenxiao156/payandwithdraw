package com.cmit.payandwithdraw.domain;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pay implements Serializable {
    private String requestId;
    private Date requestTime;
    private String salerId;
    private float totalFee;
    private Date responseTime;
    private String responseId;
    private ResponseCode responseCode;
    private String responseMsg;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return df.format(requestTime);
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public float getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(float totalFee) {
        this.totalFee = totalFee;
    }

    public String getResponseTime() {
        return responseTime == null ? null : df.format(responseTime);
    }

    public void setResponseTime(Date responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String toString() {
        return "requestId:" + this.requestId + ",salerId:" + this.salerId + ",requestTime" + getRequestTime()
                + ",totalFee" + this.totalFee + ",responseId:"+ this.responseId + ",responseTime:" + getResponseTime()
                + ",responseCode：" + getResponseCode() + ",responseMsg:" + this.responseMsg;
    }
}
