package com.myblog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.myblog.Common.Result;
import com.myblog.Dto.ArticleAddDto;
import com.myblog.Dto.ArticlePageDto;
import com.myblog.VO.ArticleAddVO;
import com.myblog.VO.ArticlePageInfoVO;
import com.myblog.VO.PageVO;
import com.myblog.pojo.Article;

public interface ArticleService extends IService<Article> {
    Result<ArticleAddVO> articleAdd(ArticleAddDto articleAddDto);

    Result<PageVO<ArticlePageInfoVO>> articleQueryPages(ArticlePageDto articlePageDto);
}
