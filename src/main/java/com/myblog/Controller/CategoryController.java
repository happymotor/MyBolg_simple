package com.myblog.Controller;

import com.myblog.Common.Result;
import com.myblog.Common.RoleConstants;
import com.myblog.Dto.CategoryAddDto;
import com.myblog.Dto.CategoryQueryDto;
import com.myblog.Service.CategoryService;
import com.myblog.Utils.ThreadLocalUtil;
import com.myblog.Utils.UserHolderUtil;
import com.myblog.VO.CategoryQueryVO;
import com.myblog.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    //栏目新增接口
    @PostMapping("/admin/category")
    public Result categoryAdd(@RequestBody @Validated CategoryAddDto categoryAddDto){
        //判断是否已经存在该栏目
        if(categoryService.getByCategoryName(categoryAddDto.getCategoryName())!=null){
            return Result.fail("不能新增已存在的栏目");
        }
        Category category=new Category();
        category.setCategoryName(categoryAddDto.getCategoryName());
        category.setStatus(categoryAddDto.getStatus());
        categoryService.categoryAdd(category);
        return Result.success();
    }

    //栏目列表查询（非分页）
    @GetMapping("/category/list")
    public Result<List<CategoryQueryVO>> categoryQuery(CategoryQueryDto categoryQueryDto){
        List<Long> roleIds= UserHolderUtil.getUserHolderRoleIds();
        Boolean hasAdminRole=roleIds.contains(RoleConstants.ROLE_ID_ADMIN)
                ||roleIds.contains(RoleConstants.ROLE_ID_ROOT);
        return Result.success(categoryService.categoryQuery(categoryQueryDto,hasAdminRole));
    }


}
