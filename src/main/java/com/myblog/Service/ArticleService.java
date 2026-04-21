package com.myblog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.Common.Result;
import com.myblog.Dto.ArticleAddDto;
import com.myblog.VO.ArticleAddVO;
import com.myblog.pojo.Article;

public interface ArticleService extends IService<Article> {
    Result<ArticleAddVO> articleAdd(ArticleAddDto articleAddDto);
}
