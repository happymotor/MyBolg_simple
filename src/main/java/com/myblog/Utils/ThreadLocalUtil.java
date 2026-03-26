package com.myblog.Utils;

/**
 * ThreadLocal 工具类
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {

    //提供ThreadLocal工具类对象
    private static final ThreadLocal THREAD_LOCAL=new ThreadLocal();

    //创建键获取值
    public static <T> T get(){ return (T) THREAD_LOCAL.get();}

    //存储键值对
    public static void  set(Object obj){ THREAD_LOCAL.set(obj); }

    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
