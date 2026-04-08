package com.myblog.Controller;


import com.myblog.Common.Result;
import com.myblog.Dto.UserPageDto;
import com.myblog.Dto.UserRoleAssignDto;
import com.myblog.Dto.UserStatusUpdateDto;
import com.myblog.Service.AdminUserService;
import com.myblog.Service.RoleService;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import com.myblog.pojo.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;


    //用户分页列表查询接口
    @GetMapping("/list")
    public Result<PageVO<UserInfoVO>> userQueryPages(@Validated UserPageDto userPageDto){
        return Result.success(adminUserService.userQueryPages(userPageDto));
    }


    //用户状态修改接口
    @PatchMapping("/status/{userId}")
    public Result userStatusUpdate(@PathVariable Long userId,
                                   @RequestBody @Validated UserStatusUpdateDto userStatusUpdateDto){
        User user = adminUserService.getByUserId(userId);
        if(user==null){
            return Result.fail("该用户不存在");
        }
        Byte status = userStatusUpdateDto.getStatus();
        if(status!=0&&status!=1){
            return Result.fail("status只能为0或者1");
        }
        if(status.equals(user.getStatus())){
            return Result.fail("修改状态不可以与用户当前状态一致");
        }
        if(userId==1L){
            return Result.fail("不可以修改root状态");
        }

        user.setStatus(status);
        adminUserService.userStatusUpdate(user);
        return Result.success();
    }

    //用户角色分配接口
    @PutMapping("/role/{userId}")
    public  Result UserRoleAssign(@PathVariable Long userId,
                                  @RequestBody @Validated UserRoleAssignDto userRoleAssignDto){
         List<Long> roleIds=userRoleAssignDto.getRoleIds();

         if(userId==null){
             return Result.fail("用户id不能为空");
         }

        User user = adminUserService.getByUserId(userId);
        if(user==null){
            return Result.fail("该用户不存在");
        }

         if(roleIds.contains(1L)){
             return Result.fail("不可以分配Root角色给任意用户");
         }

         adminUserService.userRoleAssign(userId,roleIds);

         return Result.success();
    }

     //用户删除接口
     @DeleteMapping("/{userId}")
     public Result userDelete(@PathVariable(name="userId") List<Long> userIds){
        if(userIds==null||userIds.isEmpty()){
            return Result.fail("id参数不可以为空");
        }
        for(Long userId:userIds){
            if(userId==1L){
                return Result.fail("不可以删除root");
            }
            User user=adminUserService.getByUserId(userId);
            if(user==null){
                return Result.fail("该用户不存在");
            }
            adminUserService.userDelete(user);
        }
        return Result.success();
     }

}
