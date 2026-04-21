package com.myblog.Controller;

import com.myblog.Common.Result;
import com.myblog.Dto.ArticleAddDto;
import com.myblog.Service.ArticleService;
import com.myblog.Utils.MarkdownUtil;
import com.myblog.VO.ArticleAddVO;
import com.myblog.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result<ArticleAddVO> articleAdd(@RequestBody  @Validated ArticleAddDto articleAddDto){

        return articleService.articleAdd(articleAddDto);
    }

}
