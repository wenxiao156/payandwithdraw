package com.cmit.payandwithdraw.domain;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AuditDAO {
    String TABLE_NAME = "audit";
    String FIELDS = "saler_id, balance, audit_result, audit_money";

    //根据[id]查找
    @Select({"select ", FIELDS, " from ", TABLE_NAME, " where saler_id = #{id}"})
    Audit selectById(String id);

    //查找全部列表
    @Select({"select ", FIELDS, " from ", TABLE_NAME})
    List<Audit> findAll();

    //增加稽核记录
    @Insert({"insert into ", TABLE_NAME, "(", FIELDS, ") values (#{salerId}, #{balance},#{auditResult}, #{auditMoney})"})
    int addAudit(Audit audit);

    //删除稽核记录
    @Delete({"delete from ", TABLE_NAME, " where saler_id=#{salerId}"})
    void deleteAudit(String salerId);

    //更新稽核记录
    @Update({"update ", TABLE_NAME, " set audit_result =#{auditResult}, balance =#{balance}, audit_money =#{auditMoney} where saler_id=#{salerId}"})
    void updateAudit(Audit audit);
}
