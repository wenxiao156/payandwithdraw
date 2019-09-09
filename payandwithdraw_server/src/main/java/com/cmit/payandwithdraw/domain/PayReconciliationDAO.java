package com.cmit.payandwithdraw.domain;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface PayReconciliationDAO {
    String TABLE_NAME = "payreconciliation";
    String FIELDS = "request_id, response_id, total_fee, diff_type";

    //根据[id]查找
    @Select({"select ", FIELDS, " from ", TABLE_NAME, " where response_id = #{id}"})
    PayReconciliation selectById(String id);

    //查找全部列表
    @Select({"select ", FIELDS, " from ", TABLE_NAME})
    List<PayReconciliation> findAll();

    //增加对账记录
    @Insert({"insert into ", TABLE_NAME, "(", FIELDS, ") values (#{requestId}, #{responseId},#{totalFee}, #{diffType})"})
    int addPay(PayReconciliation pay);

    //删除对账记录
    @Delete({"delete from ", TABLE_NAME, " where response_id=#{responseId} and request_id=#{requestId}"})
    void deletePay(String responseId,String requestId);

    //更新对账记录
    @Update({"update ", TABLE_NAME, " set diff_Type =#{diffType} where response_id=#{responseId} and request_id=#{requestId}"})
    void updatePay(PayReconciliation pay);
}
