package com.myblog.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Mapper.ArticleTagMapper;
import com.myblog.Service.ArticleTagService;
import com.myblog.pojo.ArticleTag;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
