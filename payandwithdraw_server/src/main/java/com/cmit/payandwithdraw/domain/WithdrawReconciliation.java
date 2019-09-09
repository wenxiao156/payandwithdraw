package com.cmit.payandwithdraw.domain;

public class WithdrawReconciliation {
    private String requestId;
    private String responseId;
    private float withdrawMoney;
    private DiffType diffType;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public float getWithdrawMoney() {
        return withdrawMoney;
    }

    public void setWithdrawMoney(float withdrawMoney) {
        this.withdrawMoney = withdrawMoney;
    }

    public DiffType getDiffType() {
        return diffType;
    }

    public void setDiffType(DiffType diffType) {
        this.diffType = diffType;
    }
}
