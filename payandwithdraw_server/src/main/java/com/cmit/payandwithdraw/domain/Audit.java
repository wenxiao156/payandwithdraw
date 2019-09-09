package com.cmit.payandwithdraw.domain;

public class Audit {
    private String salerId;
    private float balance;
    private ResponseCode auditResult;
    private float auditMoney;

    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public ResponseCode getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(ResponseCode auditResult) {
        this.auditResult = auditResult;
    }

    public float getAuditMoney() {
        return auditMoney;
    }

    public void setAuditMoney(float auditMoney) {
        this.auditMoney = auditMoney;
    }
}
