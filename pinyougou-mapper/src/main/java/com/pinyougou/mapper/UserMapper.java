package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.User;

import java.io.Serializable;
import java.util.Date;

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

    /** 昵称,密码保存设置 */
    @Update("update tb_user SET password = #{password},updated = #{updated},nick_name = #{nickName} where username = #{username}")
    void savePasswordOrNickname(@Param("password") String password,@Param("updated") Date updated,
                                @Param("nickName") String nickName, @Param("username") String username);

    /** 查询当前登录的用户信息 */
    @Select("select * from tb_user where username = #{userId}")
    User findUser(String userId);

    /** 修改绑定后的手机号码 */
    @Update("update tb_user set phone = #{phone} where username = #{userId}")
    void updatePhone(@Param("phone")String phone, @Param("userId")String userId);

}