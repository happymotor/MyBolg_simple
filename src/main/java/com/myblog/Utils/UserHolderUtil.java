package com.myblog.Utils;

import com.myblog.Common.RoleConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserHolderUtil {
    private  UserHolderUtil(){}

    //绝对禁止这样子，否则claims只记录固定的第一个用户的信息，后续换成其他用户也不会更新，同时线程也不安全
    //private static final Map<String,Object> claims=ThreadLocalUtil.get();

    public static String getUserHolderName(){
        Map<String,Object> claims=ThreadLocalUtil.get();
        return claims.get("username").toString();
    }

    public static Long getUserHolderId(){
        Map<String,Object> claims=ThreadLocalUtil.get();
        return ((Number)claims.get("userId")).longValue();
    }

    public static Long getUserHolderStatus(){
        //ps:当 userId 数值在 Integer 范围（-2147483648 ~ 2147483647）内时，
        // claims.get("userId") 返回的是 Integer 类型,不能直接把Integer强制转换成是Long
        Map<String,Object> claims=ThreadLocalUtil.get();
        return ((Number)claims.get("status")).longValue();
    }

    public static Boolean getUserHolderIsDeleted(){
        Map<String,Object> claims=ThreadLocalUtil.get();
        return (Boolean) claims.get("isDeleted");
    }


    public static List<Long> getUserHolderRoleIds(){
        Map<String,Object> claims=ThreadLocalUtil.get();
        //不能直接强转泛型列表，泛型擦除会造成运行时不匹配
        Object roleIdsObj=claims.get("roleIds");
        if(!(roleIdsObj instanceof List<?>)){
            return Collections.emptyList();
        }
        List<?> tempList=(List<?>)roleIdsObj;
        List<Long> resultList=new ArrayList<>();
        for(Object obj:tempList){
            if(obj instanceof Long){
                resultList.add((Long)obj);
            }else if (obj instanceof Integer) {
                // 兼容Integer类型，自动转成Long（兜底）
                resultList.add(((Integer) obj).longValue());
            }
        }
        return resultList;
    }

    public static boolean isAdmin(){
        return getUserHolderRoleIds().contains(RoleConstants.ROLE_ID_ADMIN)
                ||getUserHolderRoleIds().contains(RoleConstants.ROLE_ID_ROOT);
    }

}
