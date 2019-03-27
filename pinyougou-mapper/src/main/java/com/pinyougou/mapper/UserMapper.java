package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

import java.io.Serializable;

/**
 * UserMapper 数据访问接口
 * @date 2019-02-27 09:55:07
 * @version 1.0
 */
public interface UserMapper extends Mapper<User>{

    /** 根据用户名查询用户信息 */
    @Select("select * from tb_user where username= #{username}")
    User findUserId(@Param("username") String userName);

    /** 根据用户名查询用户信息 */
    @Select("select id from tb_user where username= #{username}")
    Long findId(String username);
}