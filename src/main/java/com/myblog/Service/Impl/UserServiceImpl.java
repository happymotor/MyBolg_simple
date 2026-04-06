package com.myblog.Service.Impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.myblog.Common.RoleConstants;
import com.myblog.Dto.UserRegisterDto;
import com.myblog.Mapper.UserMapper;
import com.myblog.Service.UserRoleService;
import com.myblog.Service.UserService;

import com.myblog.Utils.JwtUtil;
import com.myblog.Utils.Md5Util;

import com.myblog.VO.UserInfoVO;
import com.myblog.VO.UserLoginVO;
import com.myblog.VO.UserTokenVO;
import com.myblog.pojo.User;
import com.myblog.pojo.UserRole;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TokenRedisService tokenRedisService;

    @Autowired
    private UserRoleService userRoleService;


    @Override
    public User getByUserName(String username) {
        return userMapper.getByUserName(username);
    }

    @Override
    public UserLoginVO userLogin(User user, Boolean rememberMe) {
        //创建载荷
        Map<String,Object> claims=new HashMap<>();
        claims.put("userId",user.getUserId());
        claims.put("username",user.getUsername());
        claims.put("status",user.getStatus());
        claims.put("isDeleted",user.getIsDeleted());

        //生成token
        String accessToken=JwtUtil.generateAccessToken(claims);
        String refreshToken=rememberMe
                ?JwtUtil.generateRefreshTokenLong(claims)
                :JwtUtil.generateRefreshToken(claims);

        //把用户当前的所持有的所有token存入reids中便于后续管理
        tokenRedisService.saveAccessToken(user.getUserId(),accessToken);
        if (rememberMe) {
            tokenRedisService.saveRefreshLongToken(user.getUserId(), refreshToken);
        } else {
            tokenRedisService.saveRefreshToken(user.getUserId(), refreshToken);
        }

        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setAccessToken(accessToken);
        userLoginVO.setRefreshToken(refreshToken);
        userLoginVO.setUserInfoVO(BeanUtil.copyProperties(user, UserInfoVO.class));

        return userLoginVO;
    }

    @Override
    public void userRegister(UserRegisterDto userRegisterDto) {
        User user = new User();
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(Md5Util.getMD5String(userRegisterDto.getPassword()));
        user.setNickname(userRegisterDto.getNickname());
        user.setEmail(userRegisterDto.getEmail());
        //如果为null或者空集合，则赋值
        if(user.getRoles().isEmpty()){
            user.getRoles().add(RoleConstants.ROLE_CODE_USER);
        }
        this.save(user);
        UserRole userRole=new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(RoleConstants.ROLE_ID_USER);
        userRoleService.save(userRole);
    }

    @Override
    public UserTokenVO getUserTokenVO(String refreshToken) {
        //解析令牌
        Map<String,Object> claims = JwtUtil.parseToken(refreshToken);
        String newAccessToken = JwtUtil.generateAccessToken(claims);

        //把用户当前的刷新得到的accessToken存入reids中便于后续管理
        //ps:当 userId 数值在 Integer 范围（-2147483648 ~ 2147483647）内时，
        // claims.get("userId") 返回的是 Integer 类型,不能直接把Integer强制转换成是Long
        Long userId= ((Number)claims.get("userId")).longValue();
        tokenRedisService.saveAccessToken(userId,newAccessToken);

        return new UserTokenVO(newAccessToken,refreshToken);
    }

    @Override
    public void userLogout(HttpServletRequest request) {
        //获取请求头，令牌验证
        //去掉“Bearer ”
        String accessToken=request.getHeader("Authorization").substring(7);
        tokenRedisService.addTokenToBlackList(accessToken);
    }

}
