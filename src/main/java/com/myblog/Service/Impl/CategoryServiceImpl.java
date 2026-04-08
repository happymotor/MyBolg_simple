package com.myblog.Service.Impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Dto.CategoryQueryDto;
import com.myblog.Mapper.CategoryMapper;
import com.myblog.Service.CategoryService;
import com.myblog.VO.CategoryQueryVO;
import com.myblog.pojo.Category;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Override
    public Category getByCategoryName(String categoryName) {
        return lambdaQuery()
                .eq(Category::getCategoryName,categoryName)
                .one();

    }

    @Override
    public void categoryAdd(Category category) {
        this.save(category);
    }

    @Override
    public List<CategoryQueryVO> categoryQuery(CategoryQueryDto categoryQueryDto, Boolean hasAdminRole) {
        String keyword=categoryQueryDto.getKeyword();
        Byte status=hasAdminRole?categoryQueryDto.getStatus():1;

        List<Category> categoryList = lambdaQuery()
                //MyBatis-Plus 的`like`方法默认不做非空判断
                .like(StringUtils.hasText(keyword),Category::getCategoryName, keyword)
                .eq(Category::getStatus, status)
                .list();
        return BeanUtil.copyToList(categoryList, CategoryQueryVO.class);
    }
}
