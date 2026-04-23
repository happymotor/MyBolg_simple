package com.myblog.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.myblog.Common.Result;
import com.myblog.Dto.ArticleAddDto;
import com.myblog.Dto.ArticlePageDto;
import com.myblog.Mapper.ArticleMapper;
import com.myblog.Service.ArticleService;
import com.myblog.Service.ArticleTagService;
import com.myblog.Service.CategoryService;
import com.myblog.Service.TagService;
import com.myblog.Utils.MarkdownUtil;
import com.myblog.Utils.UserHolderUtil;
import com.myblog.VO.ArticleAddVO;
import com.myblog.VO.ArticlePageInfoVO;
import com.myblog.VO.PageVO;
import com.myblog.pojo.Article;
import com.myblog.pojo.ArticleTag;
import com.myblog.pojo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Override
    public Result<PageVO<ArticlePageInfoVO>> articleQueryPages(ArticlePageDto articlePageDto) {
        //参数校验
        if(articlePageDto.getStatus()!=0&&articlePageDto.getStatus()!=1&&articlePageDto.getStatus()!=2){
            return Result.fail("status该状态不不存在");
        }

        //进行数据库分页查询
        String keyword=articlePageDto.getKeyword();
        Byte status=articlePageDto.getStatus();
        Long categoryId=articlePageDto.getCategoryId();
        Long userId=UserHolderUtil.getUserHolderId();
        if(articlePageDto.getUserId()!=null&&UserHolderUtil.isAdmin()){
            userId=articlePageDto.getUserId();
        }
        //构建分页条件
        Page<Article> page=Page.of(articlePageDto.getPageNum(),articlePageDto.getPageSize());

        //排序规则
        page.addOrder( OrderItem.desc("view_count"));

        //分页查询
        Page<Article> p=lambdaQuery().eq(Article::getStatus,status)
                .eq(categoryId!=null,Article::getCategoryId,categoryId)
                .eq(Article::getAuthorId,userId)
                .like(keyword!=null,Article::getTitle,keyword)
                .page(page);

        //封装VO结果
        PageVO<ArticlePageInfoVO> vo=new PageVO<>();
        //总条数
        vo.setTotal(p.getTotal());
        //当前页数据
        if(CollUtil.isEmpty(p.getRecords())){
            vo.setList(Collections.emptyList());
            return Result.success(vo);
        }
        //将分页数据转换成ArticlePageInfoVO，收集成链表
        List<ArticlePageInfoVO> list= BeanUtil.copyToList(p.getRecords(), ArticlePageInfoVO.class);
        vo.setList(list);
        //页号
        vo.setPageNum(articlePageDto.getPageNum());
        //每页条数
        vo.setPageSize(articlePageDto.getPageSize());
        //总页数
        vo.setPages(p.getPages());

        vo.calculatePageInfo();
        //4.返回
        return Result.success(vo);
    }
}
