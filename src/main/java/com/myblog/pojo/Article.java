package com.myblog.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.myblog.Common.RegexPatternsConstants;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long articleId;

    /**
     * 文章标题
     */
    @NotEmpty
    @Pattern(regexp = RegexPatternsConstants.ARTICLE_TITLE_REGEX,
             message = "标题长度需要在长度1~100位")
    private String title;

    /**
     * Markdown格式文章正文
     */
    @NotEmpty
    private String content;

    /**
     * 渲染后的HTML格式正文
     */
    private String htmlContent;

    /**
     * 文章摘要
     */
    @Pattern(regexp = RegexPatternsConstants.ARTICLE_SUMMARY_REGEX,
            message = "摘要长度需要在长度1~100位")
    private String summary;

    /**
     * 所属栏目ID
     */
    @NotNull
    private Long categoryId;

    /**
     * 作者用户ID
     */
    private Long authorId;

    /**
     * 文章状态：0-草稿，1-已发表，2-回收站
     */
    @NotNull
    private Byte status;

    /**
     * 浏览量PV
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 收藏数
     */
    private Long collectCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 创建时间
     * 插入时自动填充
     */

    private LocalDateTime createTime;

    /**
     * 更新时间
     * 插入和更新时自动填充
     */

    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("is_deleted")
    private Boolean isDeleted;

}
