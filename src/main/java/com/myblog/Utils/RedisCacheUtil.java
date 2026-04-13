package com.myblog.Utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.myblog.Common.LockPrefixConstants;
import com.myblog.Common.RedisData;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class RedisCacheUtil {
    private RedisCacheUtil(){}

    //NULL值缓存的过期时间（分钟）
    private static Long CACHE_NULL_TTL = 5L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //引入线程池
    private static final ExecutorService CACHE_REBUILD_EXECUTOR= Executors.newFixedThreadPool(10);

    //获取锁
    private boolean tryLock(String key){
        //selfIfAbsent:只要key不存在时，才会设置成功
        //设置10s过期防止死锁
        Boolean flag=stringRedisTemplate.opsForValue().setIfAbsent(key,"1",10,TimeUnit.SECONDS);
        return Boolean.TRUE.equals(flag);
    }

    //释放锁
    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }


    //设置自动过期时间
    public  void set(String key, Object value, Long time, TimeUnit timeUnit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,timeUnit);
    }

    //逻辑过期
    public  void setWithLogicalExpire(String key, Object value, Long time, TimeUnit timeUnit){
        RedisData redisData=new RedisData();
        redisData.setData(value);
        redisData.setExpire(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    //解决缓存穿透的查询
    // Function<ID,T>为函数式编程,ID为返回值，T为传参
    public <T,ID> T queryWithPassThrough(
            String keyPrefix, ID id, Class<T> type, Function<ID,T> dbFallBack,Long time, TimeUnit timeUnit
            ){
        String key=keyPrefix+id;
        //从Redis中查询缓存
        String json=stringRedisTemplate.opsForValue().get(key);
        //判断是否存在
        if(StrUtil.isNotBlank(json)){
            //存在，直接返回
            return JSONUtil.toBean(json,type);
        }
        //判断是否为空
        if(json!=null){
            //返回一个错误信息
            return null;
        }

        //redis中不存在，根据id查询数据库
        T t=dbFallBack.apply(id);
        if(t==null){
            //将空值写入redis解决缓存穿透
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES);
            //返回错误信息
            return null;
        }
        //数据库查询存在则更新到redis中
        this.set(key,t,time,timeUnit);
        return t;
    }

    //针对于逻辑删除的查询
    public <T,ID> T queryWithLogicalExpire(
            String keyPrefix, ID id, Class<T> type, TypeReference<T> typeRef, Function<ID,T> dbFallBack, Long time, TimeUnit timeUnit
    ){
        String key=keyPrefix+id;
        String json=stringRedisTemplate.opsForValue().get(key);
        //缓存未命中
        if(StrUtil.isBlank(json)){
            // 1. 先查数据库
            T t = dbFallBack.apply(id);
            // 2. 数据库里也没有，直接返回null
            if (t == null) {
                return null;
            }
            // 3. 数据库里有，写入逻辑过期缓存
            this.setWithLogicalExpire(key, t, time, timeUnit);
            // 4. 直接返回数据
            return t;
        }

        //缓存命中
        RedisData redisData=JSONUtil.toBean(json, RedisData.class);
        //提取实际数据
        // 先把data字段转成JSON字符串，再用TypeReference反序列化，自动适配对象/数组
        String dataJson = JSONUtil.toJsonStr(redisData.getData());
        T t = JSONUtil.toBean(dataJson, typeRef, false);
        //提取逻辑过期时间
        LocalDateTime expire=redisData.getExpire();

        //判断是否过期
        if(expire.isAfter(LocalDateTime.now())){
            //说明没过期，直接返回数据
            return  t;
        }
        //否则，已经过期,需要重建缓存
        //先获取分布式锁
        String lockKey= LockPrefixConstants.LOCK_CATEGORY_PREFIX+id;
        boolean isLock=tryLock(lockKey);

        //拿到锁了
        if(isLock){
            //开启异步线程,更新缓存,不阻塞当前调用
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try{
                    //查数据库
                    T newT=dbFallBack.apply(id);
                    //重建缓存
                    this.setWithLogicalExpire(key,newT,time,timeUnit);
                }catch (Exception e){
                    throw new RuntimeException();
                }finally {
                    unlock(lockKey);
                }
            });
        }
        //无论是否有没有拿到锁，都直接返回旧数据
        return t;
    }

    //批量删除
    public  void deleteBatch(String keyPattern){
        //获取所有匹配的keys
        Set<String> keys=stringRedisTemplate.keys(keyPattern);
        if(keys!=null&&!keys.isEmpty()){
            //批量删除
            stringRedisTemplate.delete(keys);
        }
    }
}
