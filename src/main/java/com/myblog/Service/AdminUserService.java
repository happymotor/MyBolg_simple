package com.myblog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.Dto.UserPageDto;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import com.myblog.pojo.User;



public interface AdminUserService extends IService<User> {
    //用户分页查询
    PageVO<UserInfoVO> userQueryPages(UserPageDto userPageDto);
}
