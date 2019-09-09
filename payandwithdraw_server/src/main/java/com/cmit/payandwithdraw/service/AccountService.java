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

    public Account selectById(String id){
        return accountDAO.selectById(id);
    }

    public List<Account> findAll() {
        return accountDAO.findAll();
    }

    public void updateAccount(Account account) {
        accountDAO.updateAccount(account);
    }



}
