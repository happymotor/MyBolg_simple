package com.myblog.Controller;

import cn.hutool.core.bean.BeanUtil;
import com.myblog.Dto.PageDto;
import com.myblog.Dto.Result;
import com.myblog.Dto.UserPageDto;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import com.myblog.VO.UserLoginVO;
import com.myblog.Dto.UserRegisterDto;
import com.myblog.Service.UserService;
import com.myblog.Utils.JwtUtil;
import com.myblog.Utils.Md5Util;
import com.myblog.Utils.RedisPrefixUtil;
import com.myblog.VO.UserTokenVO;
import com.myblog.pojo.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class UserController {

     @Autowired
     private UserService userService;

     @Resource
     private StringRedisTemplate stringRedisTemplate;

     //测试方法
    @GetMapping("/hello")
    public String sayHello(){
        System.out.println("hello,nihao");
        return "Hello...";
    }

     //用户登录接口
     @PostMapping("/login")
     public Result<UserLoginVO> userLogin(@RequestBody Map<String, Object> map){
          String username=(String)map.get("username");
          String password=(String)map.get("password");
          Boolean rememberMe=(Boolean) map.get("rememberMe");
          //输入了空用户名或者密码，则登录失败
          if(!StringUtils.hasLength(username)||!StringUtils.hasLength(password)){
               return Result.fail("用户名或者密码错误");
          }

          User user=userService.getByUserName(username);
          //用户不存在
          if(user==null ){
               return Result.fail("用户名或者密码错误");
          }
          //校验密码通过
          if(Md5Util.getMD5String(password).equals(user.getPassword())){
              Map<String,Object> claims=new HashMap<>();
              claims.put("userId",user.getUserId());
              claims.put("username",username);

              //生成token响应
              String accessToken=JwtUtil.generateAccessToken(claims);
              String refreshToken=rememberMe
                                    ?JwtUtil.generateRefreshTokenLong(claims)
                                    :JwtUtil.generateRefreshToken(claims);

              UserLoginVO userLoginVO = new UserLoginVO();
              userLoginVO.setAccessToken(accessToken);
              userLoginVO.setRefreshToken(refreshToken);
              userLoginVO.setUserInfoVO(BeanUtil.copyProperties(user, UserInfoVO.class));

              return Result.success(userLoginVO);
          }

          //校验密码不通过
          return Result.fail("用户名或者密码错误");

     }



     //用户注册接口
     @PostMapping("/register")
     public Result userRegister(@RequestBody @Validated UserRegisterDto userRegisterDto){

        //检验是否用户名已存在
         if(userService.getByUserName(userRegisterDto.getUsername())!=null){
                return Result.fail("用户名已存在");
         }

         //检验两次密码输入是否相同
         if(!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())){
             return Result.fail("两次密码输入不一致！");
         }

         //判断nickname是否为空，为空则设置为用户名
         if(!StringUtils.hasLength(userRegisterDto.getNickname())){
             userRegisterDto.setNickname(userRegisterDto.getUsername());
         }
         //调用userService层完成注册操作
         userService.userRegister(userRegisterDto);
         return Result.success();
     }


     //刷新accessToken令牌方法
     @PostMapping("/refresh")
     public Result<UserTokenVO> tokenRefresh(@RequestBody Map<String,Object> map){
        String refreshToken = map.get("refreshToken").toString();
        //检验不为空
        if(refreshToken==null){
            return Result.fail("refreshToken不能为空");
        }
        //解析令牌
        Map<String,Object> claims = JwtUtil.parseToken(refreshToken);
        String newAccessToken = JwtUtil.generateAccessToken(claims);
        return Result.success(new UserTokenVO(newAccessToken,refreshToken));
     }


      //用户登出接口
      @PostMapping("/logout")
      public Result userLogout(HttpServletRequest request){
        //获取请求头，令牌验证
        String authHeader=request.getHeader("Authorization");
        //解析令牌
        // 没有请求头或者token格式不对
        if(authHeader==null||!authHeader.startsWith("Bearer ")){
            return Result.fail("请求错误");
        }
        //去掉“Bearer ”
        String accessToken=authHeader.substring(7);
        //解析获取过期时间
        long remainingTime=JwtUtil.getRemainingTimes(accessToken);
        if(remainingTime>0){
            String redisKey=RedisPrefixUtil.BLACKLIST_KEY_PREFIX+accessToken;
            stringRedisTemplate.opsForValue().set(redisKey,"black",remainingTime, TimeUnit.MILLISECONDS);
        }
        return Result.success();
     }



}