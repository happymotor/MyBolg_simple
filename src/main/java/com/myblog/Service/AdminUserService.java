package com.myblog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.Dto.UserPageDto;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import com.myblog.pojo.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


public interface AdminUserService extends IService<User> {
    //用户分页查询
    PageVO<UserInfoVO> userQueryPages(UserPageDto userPageDto);

    //根据用户username查询返回User对象
    User getByUserName(String username);

    //根据用户userId查询返回User对象
    User getByUserId(Long userId);

    //更新用户状态
    void userStatusUpdate(User user);

    //用户角色分配
    void userRoleAssign(Long userId, List<Long> roleIds);

    //删除用户
    void userDelete(User user);
}
