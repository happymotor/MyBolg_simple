package com.myblog.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Dto.UserPageDto;
import com.myblog.Mapper.AdminUserMapper;
import com.myblog.Service.AdminUserService;
import com.myblog.Service.RoleService;
import com.myblog.Service.UserRoleService;
import com.myblog.Service.UserService;
import com.myblog.VO.PageVO;
import com.myblog.VO.UserInfoVO;
import com.myblog.pojo.User;
import com.myblog.pojo.UserRole;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, User> implements  AdminUserService {

    @Autowired
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TokenRedisService tokenRedisService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;


    @Override
    public User getByUserName(String username) {
        return userService.getByUserName(username);
    }

    @Override
    public User getByUserId(Long userId) {
        return this.getById(userId);
    }

    @Override
    public PageVO<UserInfoVO> userQueryPages(UserPageDto userPageDto) {
        String keyword = userPageDto.getKeyword();
        Byte status = userPageDto.getStatus();
        //1.构建分页条件
        Page<User> page=Page.of(userPageDto.getPageNum(),userPageDto.getPageSize());
        //排序规则
        page.addOrder( OrderItem.desc("update_time"));

        //2.分页查询
        Page<User> p = lambdaQuery().eq(status != null, User::getStatus, status)
                .like(keyword != null, User::getUsername, keyword)
                .page(page);

        //3.封装VO结果
        PageVO<UserInfoVO> vo=new PageVO<>();
        //总条数
        vo.setTotal(p.getTotal());
        //当前页数据
        if(CollUtil.isEmpty(p.getRecords())){
            vo.setList(Collections.emptyList());
            return vo;
        }
        List<UserInfoVO> list= BeanUtil.copyToList(p.getRecords(),UserInfoVO.class);
        vo.setList(list);
        //页号
        vo.setPageNum(userPageDto.getPageNum());
        //每页条数
        vo.setPageSize(userPageDto.getPageSize());
        //总页数
        vo.setPages(p.getPages());

        vo.calculatePageInfo();
        //4.返回
        return vo;
    }

    // 将存于redis中的该user对象的所有token取出并加入到黑名单
    @Override
    public void userStatusUpdate(User user) {
        updateById(user);
        //判断如果修改前是禁用状态（0）就不进行下面步骤，因为之前肯定没有jwt令牌
        if(user.getStatus()==1){
            return;
        }
        Long userId=user.getUserId();

        tokenRedisService.addAllTokensToBlackList(userId);
    }

    @Override
    public void userRoleAssign(Long userId, List<Long> roleIds) {
        User user=getByUserId(userId);
        //千万不可以用user.setRoles(null);
        user.getRoles().clear();

        //删除用户角色表中该用户的原数据
        userRoleService.remove(
                new LambdaQueryWrapper<UserRole>()
                        .eq(UserRole::getUserId,userId)
        );

        for(Long roleId:roleIds){
            String roleCode=roleService.getRoleById(roleId).getRoleCode();
            user.getRoles().add(roleCode);
            //更新用户角色表
            UserRole userRole=new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleService.save(userRole);
        }
        //更新用户中role信息
        updateById(user);
    }

    @Override
    public void userDelete(User user) {
        user.setIsDeleted(Boolean.TRUE);
        //逻辑删除，所以不用真正的删除，只需要更改用户属性is_deleted
        userService.updateById(user);
        tokenRedisService.addAllTokensToBlackList(user.getUserId());
    }

}
