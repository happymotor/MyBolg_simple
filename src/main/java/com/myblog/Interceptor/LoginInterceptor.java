package com.myblog.Interceptor;

import cn.hutool.json.JSONUtil;
import com.myblog.Utils.JwtUtil;
import com.myblog.Common.RedisPrefixConstants;
import com.myblog.Utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                String responseJson= JSONUtil.createObj()
                        .set("code",401)
                        .set("msg","jwt令牌无效")
                        .toString();
                response.getWriter().write(responseJson);
                return false;
            }

            //去掉“Bearer ”
            String accessToken=authHeader.substring(7);

            //校验是否为黑名单令牌
            String redisKey= RedisPrefixConstants.BLACKLIST_KEY_PREFIX+accessToken;
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))){
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                String responseJson= JSONUtil.createObj()
                        .set("code",401)
                        .set("msg","jwt令牌已失效")
                        .toString();
                response.getWriter().write(responseJson);
                return false;
            }

            //获取自定义载荷信息
            Map<String ,Object> claims= JwtUtil.parseToken(accessToken);

            //将数据存储到ThreadLocal
            ThreadLocalUtil.set(claims);
            //成功
            return true;

        }catch (Exception e){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            String responseJson= JSONUtil.createObj()
                    .set("code",401)
                    .set("msg","jwt令牌无效")
                    .toString();
            response.getWriter().write(responseJson);
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
