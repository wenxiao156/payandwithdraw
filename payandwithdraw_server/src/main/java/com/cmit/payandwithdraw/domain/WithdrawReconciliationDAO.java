package com.cmit.payandwithdraw.domain;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface WithdrawReconciliationDAO {
    String TABLE_NAME = "withdrawreconciliation";
    String FIELDS = "request_id, response_id, withdraw_money, diff_type";

    //根据[id]查找
    @Select({"select ", FIELDS, " from ", TABLE_NAME, " where response_id = #{id}"})
    WithdrawReconciliation selectById(String id);

    //查找全部列表
    @Select({"select ", FIELDS, " from ", TABLE_NAME})
    List<WithdrawReconciliation> findAll();

    //增加对账记录
    @Insert({"insert into ", TABLE_NAME, "(", FIELDS, ") values (#{requestId}, #{responseId},#{withdrawMoney}, #{diffType})"})
    int addWithdraw(WithdrawReconciliation withdraw);

    //删除对账记录
    @Delete({"delete from ", TABLE_NAME, " where response_id=#{responseId} and request_id=#{requestId}"})
    void deleteWithdraw(String responseId, String requestId);

    //更新对账记录
    @Update({"update ", TABLE_NAME, " set diff_Type =#{diffType} where response_id=#{responseId} and request_id=#{requestId}"})
    void updateWithdraw(WithdrawReconciliation withdraw);
}
