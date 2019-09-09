package com.cmit.payandwithdraw.domain;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface ClientWithdrawDAO {

    String TABLE_NAME = "clientwithdraw";
    String FIELDS = "request_id, request_time, saler_id, withdraw_money, response_id, response_time, response_code, response_msg";

    //根据[id]查找
    @Select({"select ", FIELDS, " from ", TABLE_NAME, " where request_id = #{id}"})
    ClientWithdraw selectById(String id);

    //查找全部列表
    @Select({"select ", FIELDS, " from ", TABLE_NAME})
    List<ClientWithdraw> findAll();

    //增加支付订单
    @Insert({"insert into ", TABLE_NAME, "(", FIELDS, ") values (#{requestId}, #{requestTime}, #{salerId}, #{withdrawMoney}, #{responseId}, #{responseTime}, #{responseCode}, #{responseMsg} )"})
    int addWithdraw(ClientWithdraw withdraw);

    //删除支付订单
    @Delete({"delete from ", TABLE_NAME, " where request_id=#{id}"})
    void deleteWithdraw(String id);

    //更新支付订单
    @Update({"update ", TABLE_NAME, " set response_id=#{responseId},response_time=#{responseTime},response_code=#{responseCode},response_msg=#{responseMsg} where request_id=#{requestId}"})
    void updateWithdraw(ClientWithdraw withdraw);
}
