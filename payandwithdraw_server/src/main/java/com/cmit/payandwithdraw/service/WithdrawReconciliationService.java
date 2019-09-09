package com.cmit.payandwithdraw.service;

import com.cmit.payandwithdraw.domain.WithdrawReconciliation;
import com.cmit.payandwithdraw.domain.WithdrawReconciliationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WithdrawReconciliationService {
    @Autowired
    private WithdrawReconciliationDAO withdrawReconciliationDAO;

    public WithdrawReconciliation selectById(String id){
        return withdrawReconciliationDAO.selectById(id);
    }

    public List<WithdrawReconciliation> findAll(){
        return withdrawReconciliationDAO.findAll();
    }

    public int addWithdraw(WithdrawReconciliation withdraw){
        return withdrawReconciliationDAO.addWithdraw(withdraw);
    }

    public void  deleteWithdraw(String responseId, String requestId){
        withdrawReconciliationDAO.deleteWithdraw(responseId,requestId);
    }

    public void updateWithdraw(WithdrawReconciliation withdraw){
        withdrawReconciliationDAO.updateWithdraw(withdraw);
    }
}
