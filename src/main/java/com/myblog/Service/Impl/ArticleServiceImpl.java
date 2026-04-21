package com.myblog.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Common.Result;
import com.myblog.Dto.ArticleAddDto;
import com.myblog.Mapper.ArticleMapper;
import com.myblog.Service.ArticleService;
import com.myblog.Service.ArticleTagService;
import com.myblog.Service.CategoryService;
import com.myblog.Service.TagService;
import com.myblog.Utils.MarkdownUtil;
import com.myblog.Utils.UserHolderUtil;
import com.myblog.VO.ArticleAddVO;
import com.myblog.pojo.Article;
import com.myblog.pojo.ArticleTag;
import com.myblog.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public Result<ArticleAddVO> articleAdd(ArticleAddDto articleAddDto) {
        //参数校验逻辑
        if(articleAddDto==null){
            return Result.fail("传参不能为空");
        }
        if(articleAddDto.getStatus()!=0&&articleAddDto.getStatus()!=1){
            return Result.fail("status该状态不不存在");
        }
        if(articleAddDto.getTags()!=null){
            if(articleAddDto.getTags().size()>5){
                return Result.fail("文章最多只能有五个标签");
            }
            for(String tag:articleAddDto.getTags()){
                if(tag.isEmpty()){
                    return Result.fail("标签名称不能为空");
                }
                if(tag.length()>10){
                    return Result.fail("标签长度不可以超过10");
                }
            }
        }
        if(!MarkdownUtil.isValid(articleAddDto.getContent())){
            return Result.fail("内容格式不正确");
        }
        if(categoryService.getByCategoryId(articleAddDto.getCategoryId())==null){
            return  Result.fail("该栏目不存在");
        }
        //数据处理
        if(articleAddDto.getSummary()==null){
            int subLength=Math.min(200,articleAddDto.getContent().length());
            articleAddDto.setSummary(articleAddDto.getContent().substring(0,subLength));
        }

        //保存 文章 对象到数据库
        Article article=new Article();
        article.setTitle(articleAddDto.getTitle());
        article.setContent(articleAddDto.getContent());
        article.setHtmlContent(MarkdownUtil.toHtml(articleAddDto.getContent()));
        article.setSummary(articleAddDto.getSummary());
        article.setCategoryId(articleAddDto.getCategoryId());
        article.setStatus(articleAddDto.getStatus());
        article.setAuthorId(UserHolderUtil.getUserHolderId());
        this.save(article);

        //判断标签对象是否为空
        if(articleAddDto.getTags()!=null&&!articleAddDto.getTags().isEmpty()) {
            //保存 标签 对象到数据库
            List<Tag> tags = articleAddDto.getTags().stream()
                    .filter(tagName -> {
                        if (tagService.getByTagName(tagName) != null) {
                            return false;
                        }
                        return true;
                    })
                    .map(tagName -> {
                        Tag tag = new Tag();
                        tag.setTagName(tagName);
                        return tag;
                    })
                    .toList();
            tagService.saveBatch(tags);

            //保存 文章标签 对象到数据库
            List<ArticleTag> articleTags = tags.stream()
                    .map(tag -> {
                        ArticleTag articleTag = new ArticleTag();
                        articleTag.setArticleId(article.getArticleId());
                        articleTag.setTagId(tag.getTagId());
                        return articleTag;
                    })
                    .toList();
            articleTagService.saveBatch(articleTags);

        }
        //创建VO对象
        return Result.success(new ArticleAddVO(article.getArticleId()));
    }
}
