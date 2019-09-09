package com.cmit.payandwithdraw.service;

import com.cmit.payandwithdraw.domain.PayReconciliation;
import com.cmit.payandwithdraw.domain.PayReconciliationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayReconciliationService {
    @Autowired
    private PayReconciliationDAO payReconciliationDAO;

    public PayReconciliation selectById(String id){
        return payReconciliationDAO.selectById(id);
    }

    public List<PayReconciliation> findAll(){
        return payReconciliationDAO.findAll();
    }

    public int addPay(PayReconciliation pay){
        return payReconciliationDAO.addPay(pay);
    }

    public void  deletePay(String responseId, String requestId){
        payReconciliationDAO.deletePay(responseId,requestId);
    }

    public void updatePay(PayReconciliation pay){
        payReconciliationDAO.updatePay(pay);
    }
}
