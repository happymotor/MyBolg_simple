package com.myblog.VO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ArticlePageInfoVO {
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

    //栏目名称
    private String categoryName;

    /**
     * 作者用户ID
     */
    private Long authorId;

    //作者名称
    private String authorName;

    /**
     * 文章状态：0-草稿，1-已发表，2-回收站
     */
    @NotNull
    private Byte status;

    //浏览量
    private Long viewCount;

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
}
