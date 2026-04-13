package com.myblog.Service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Common.CacheConstants;
import com.myblog.Dto.CategoryQueryDto;
import com.myblog.Mapper.CategoryMapper;
import com.myblog.Service.CategoryService;
import com.myblog.Utils.RedisCacheUtil;
import com.myblog.VO.CategoryQueryVO;
import com.myblog.pojo.Category;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private RedisCacheUtil redisCacheUtil;

    @Override
    public Category getByCategoryName(String categoryName) {
        return lambdaQuery()
                .eq(Category::getCategoryName,categoryName)
                .one();

    }

    @Override
    public void categoryAdd(Category category) {

        this.save(category);

        redisCacheUtil.deleteBatch(CacheConstants.CACHE_CATEGORY_PREFIX+"*");
    }

    @Override
    public List<CategoryQueryVO> categoryQuery(CategoryQueryDto categoryQueryDto, Boolean hasAdminRole) {

        //创建redis缓存的key
        StringBuilder key= new StringBuilder();
        if(categoryQueryDto!=null){
            key.append(categoryQueryDto.getKeyword()).append(":").append(categoryQueryDto.getStatus());
        }else{
            key.append("null:null");
        }


        List<Category> categoryList =redisCacheUtil.queryWithLogicalExpire(
                CacheConstants.CACHE_CATEGORY_PREFIX,
                key.toString(),
                // 泛型类型强转（解决List泛型擦除问题）
                (Class<List<Category>>) (Class<?>) List.class,
                new TypeReference<List<Category>>() {},
                (id)->{
                    //如果没有传任何参数，先对categoryQueryDto进行判空操作
                    String keyword=null;
                    Byte status=null;
                    if(categoryQueryDto!=null){
                        keyword= categoryQueryDto.getKeyword();
                    }
                    if(Boolean.FALSE.equals(hasAdminRole)){
                        status=(byte)1;
                    }else if(categoryQueryDto!=null){
                        status=categoryQueryDto.getStatus();
                    }
                    return lambdaQuery()
                            //MyBatis-Plus 的`like`方法默认不做非空判断
                            .like(StringUtils.hasText(keyword),Category::getCategoryName, keyword)
                            //MyBatis-Plus都不会自动做非空判断
                            .eq(Objects.nonNull(status),Category::getStatus, status)
                            .list();
                },
                CacheConstants.CACHE_CATEGORY_TTL,
                TimeUnit.MINUTES
        );
        return BeanUtil.copyToList(categoryList, CategoryQueryVO.class);

    }
}
