package com.myblog.Interceptor;

import com.myblog.Utils.JwtUtil;
import com.myblog.Utils.RedisPrefixUtil;
import com.myblog.Utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.Interceptor;
import org.apache.coyote.Response;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        //获取请求头，令牌验证
        String authHeader=request.getHeader("Authorization");

        //解析令牌
        try{
            //没有请求头或者token格式不对
            if(authHeader==null||!authHeader.startsWith("Bearer ")){
                return false;
            }

            //去掉“Bearer ”
            String accessToken=authHeader.substring(7);

            //校验是否为黑名单令牌
            String redisKey= RedisPrefixUtil.BLACKLIST_KEY_PREFIX+accessToken;
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))){
                response.setStatus(401);
                return false;
            }

            //将数据存储到ThreadLocal
            Map<String ,Object> claims= JwtUtil.parseToken(accessToken);
            ThreadLocalUtil.set(claims);
            //成功
            return true;

        }catch (Exception e){
            response.setStatus(401);
            //失败
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //释放线程
        ThreadLocalUtil.remove();
    }
}
