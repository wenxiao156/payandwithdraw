package com.cmit.payandwithdraw.service;

import com.cmit.payandwithdraw.domain.AccountDAO;
import com.cmit.payandwithdraw.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountService {
    @Autowired
    private AccountDAO  accountDAO;

    ReentrantLock lock = new ReentrantLock();
    private Integer token = 10000000;
    private int tokenNum = 10;
    static final CountDownLatch latch = new CountDownLatch(10);
    Map salerToken = new ConcurrentHashMap<String, Integer>();


    public Account selectById(String id){
        return accountDAO.selectById(id);
    }

    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    @Async(value = "token")
    public void getToken(String name) {
        Thread.currentThread().setName(name);
        lock.lock();
        if(tokenNum > 0) {
            token++;
            salerToken.put(Thread.currentThread().getName(),token);
            latch.countDown();
            tokenNum--;
        } else {
            this.token = null;
        }

//        Token token = new Token(salerId, timestamp);
//        salerToken = token.getToken();
        lock.unlock();
    }

    /**
     * 返回每个salerId对应的token值
     * @return
     */
    public Map<String, Integer> getSalerToken() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this.salerToken;
    }

}
