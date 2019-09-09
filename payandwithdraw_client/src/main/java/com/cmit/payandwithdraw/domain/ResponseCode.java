package com.cmit.payandwithdraw.domain;

public enum ResponseCode {
    FAIL,SUCCESS;
    public static ResponseCode returnCode(String str) {
        if("SUCCESS".equals(str.toUpperCase())){
            return ResponseCode.SUCCESS;
        }
        if("FAIL".equals(str.toUpperCase())){
            return ResponseCode.FAIL;
        }
        return null;
    }
}
