package com.cmit.payandwithdraw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TokenUtils implements Runnable{
    ReentrantLock lock = new ReentrantLock();
    private Integer token = 10000000;
    private int tokenNum = 10;
    Map salerToken = new ConcurrentHashMap<String, Integer>();

//    String salerId;
//    String timestamp;
//    String salerToken;

    @Override
    public void run() {
//        lock.lock();
//        if(tokenNum > 0) {
//            token++;
//            salerToken.put(Thread.currentThread().getName(),token);
//            tokenNum--;
//        } else {
//            this.token = null;
//        }
//
////        Token token = new Token(salerId, timestamp);
////        salerToken = token.getToken();
//        lock.unlock();
    }

    public int getToken() {
        return this.token;
    }

    public Map<String, Integer> getSalerToken() {
        return this.salerToken;
    }
}
