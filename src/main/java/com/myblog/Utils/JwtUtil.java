package com.myblog.Utils;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    //创建密钥
    private static final String SECRET_KEY = "hello,myblog,aaabbbcccdddeeefff12345";
    //ACCESS_TOKEN过期时间 2小时
    private static final long ACCESS_TOKEN_EXPIRE=2*60*60*1000;
    //REFRESH_TOKEN过期时间 7天
    private static final long REFRESH_TOKEN_EXPIRE=7*24*60*60*1000;
    //REFRESH_TOKEN_LONG过期时间 20天,用于登录延长时间
    private static final long REFRESH_TOKEN_LONG_EXPIRE=20*24*60*60*1000;

    // 将字符串密钥转换为 HMAC256 所需的 Key 对象
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    //生成accessToken
    public static String generateAccessToken(Map<String,Object> claims){
        return generateToken(claims,ACCESS_TOKEN_EXPIRE,"access");
    }

    //生成refreshToken
    public static String generateRefreshToken(Map<String,Object> claims){
        return generateToken(claims,REFRESH_TOKEN_EXPIRE,"refresh");
    }

    //生成refreshTokenLong
    public static String generateRefreshTokenLong(Map<String,Object> claims){
        return generateToken(claims,REFRESH_TOKEN_LONG_EXPIRE,"refresh");
    }


    //内部方法，通用生成token
    private static String generateToken(Map<String, Object> claims,long expire,String type) {
        Date now = new Date();
        Date expiryDate =new Date(now.getTime()+expire);
        claims.put("type:",type);
        return Jwts.builder()
                .setClaims(claims)//自定义载荷
                .setIssuedAt(now)//签发时间
                .setExpiration(expiryDate)//过期时间
                .signWith(KEY)//加密方法
                .compact();
    }

	//接收token,验证token,并返回业务数据
    public static Map<String, Object> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //获取剩余有效时间（毫秒）accessToken和refreshToken均适用
    public static long getRemainingTimes(String token){
        Map<String ,Object> claims=JwtUtil.parseToken(token);
        //修改将Integer转化为long的方法
        long expSecondTime = Long.parseLong(claims.get("exp").toString());
        return expSecondTime*1000-System.currentTimeMillis();
    }



}
