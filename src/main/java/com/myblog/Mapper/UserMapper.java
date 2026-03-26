package com.myblog.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.myblog.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from tb_user where username=#{username}")
    User getByUserName(String username);
}
