package com.cmit.payandwithdraw.domain;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AccountDAO {
    String TABLE_NAME = "account";
    String SELECT_FIELDS = "saler_id, money, password, name, status";
    //根据[id]查找
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where saler_id = #{id}"})
    Account selectById(String id);

    //查找全部列表
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME })
    List<Account> findAll();

    //更新账户订单
    @Update({"update ", TABLE_NAME, " set money=#{money},password=#{password},name=#{name},status=#{status} where saler_id=#{salerId}"})
    void updateAccount(Account account);
}
