package com.myblog.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.Dto.UserRegisterDto;
import com.myblog.pojo.User;

public interface UserService extends IService<User>{

    //通过username获取整个user对象
    User getByUserName(String username);

    //用户注册
    void userRegister(UserRegisterDto userRegisterDto);


}
