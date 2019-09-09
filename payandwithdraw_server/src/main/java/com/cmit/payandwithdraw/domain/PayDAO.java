package com.cmit.payandwithdraw.domain;


import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PayDAO {
    String TABLE_NAME = "pay";
    String FIELDS = "request_id, request_time, saler_id, total_fee, response_id, response_time, response_code, response_msg";

    //根据[id]查找
    @Select({"select ", FIELDS, " from ", TABLE_NAME, " where request_id = #{id}"})
    Pay selectById(String id);

    //查找全部列表
    @Select({"select ", FIELDS, " from ", TABLE_NAME})
    List<Pay> findAll();

    //增加支付订单
    @Insert({"insert into ", TABLE_NAME, "(", FIELDS, ") values (#{requestId}, #{requestTime}, #{salerId}, #{totalFee}, #{responseId}, #{responseTime}, #{responseCode}, #{responseMsg} )"})
    int addPay(Pay pay);

    //删除支付订单
    @Delete({"delete from ", TABLE_NAME, " where request_id=#{id}"})
    void deletePay(String id);

    //更新支付订单
    @Update({"update ", TABLE_NAME, " set response_id=#{responseId},response_time=#{responseTime},response_code=#{responseCode},response_msg=#{responseMsg} where request_id=#{requestId}"})
    void updatePay(Pay pay);
}
