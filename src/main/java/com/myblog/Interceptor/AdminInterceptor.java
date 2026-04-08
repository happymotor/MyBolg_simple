package com.myblog.Interceptor;

import cn.hutool.json.JSONUtil;
import com.myblog.Common.RoleConstants;
import com.myblog.Service.UserRoleService;
import com.myblog.Service.UserService;
import com.myblog.Utils.ThreadLocalUtil;
import com.myblog.pojo.User;
import com.myblog.pojo.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        Map<String,Object> claims= ThreadLocalUtil.get();
        String username= claims.get("username").toString();
        User user=userService.getByUserName(username);
        if(user==null){
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            String responseJson= JSONUtil.createObj()
                    .set("code",403)
                    .set("msg","用户信息不存在")
                    .toString();
            response.getWriter().write(responseJson);
            //失败
            return false;
        }

        // List<String> roles=user.getRoles(); 逻辑不对，role在数据库user表中不存在信息
        List<Long> roleIds=userRoleService.lambdaQuery()
                .select(UserRole::getRoleId)
                .eq(UserRole::getUserId,user.getUserId())
                .list()
                .stream()
                .map(UserRole::getRoleId)
                .toList();


        Boolean hasAdminRole=roleIds.contains(RoleConstants.ROLE_ID_ADMIN)
                            ||roleIds.contains(RoleConstants.ROLE_ID_ROOT);
        if(!hasAdminRole){
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            String responseJson= JSONUtil.createObj()
                    .set("code",403)
                    .set("msg","用户权限不足，请呼叫管理员")
                    .toString();
            response.getWriter().write(responseJson);
            //失败
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //释放线程
        ThreadLocalUtil.remove();
    }

}
