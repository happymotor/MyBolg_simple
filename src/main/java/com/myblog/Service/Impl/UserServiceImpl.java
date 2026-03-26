package com.myblog.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Dto.UserRegisterDto;
import com.myblog.Mapper.UserMapper;
import com.myblog.Service.UserService;

import com.myblog.Utils.Md5Util;
import com.myblog.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public User getByUserName(String username) {
        return userMapper.getByUserName(username);
    }

    @Override
    public void userRegister(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(Md5Util.getMD5String(userRegisterDto.getPassword()));
        user.setNickname(userRegisterDto.getNickname());
        user.setEmail(userRegisterDto.getEmail());
        userMapper.insert(user);
    }
}
