package com.myblog.Controller;


import com.myblog.Dto.Result;
import com.myblog.Dto.UserPageDto;
import com.myblog.Service.AdminUserService;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    //用户分页列表查询接口
    @GetMapping("/user/list")
    public Result<PageVO<UserInfoVO>> userQueryPages(@Validated UserPageDto userPageDto){
        return Result.success(adminUserService.userQueryPages(userPageDto));
    }

}
