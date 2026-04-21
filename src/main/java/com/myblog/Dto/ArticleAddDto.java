package com.myblog.Dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.myblog.Common.RegexPatternsConstants;
import com.myblog.pojo.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAddDto {

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
    /*
    * 标签，不超过五个，每个长度不超过十
    */
    private List<String> tags;

    /**
     * 文章状态：0-草稿，1-已发表，2-回收站
     */
    @NotNull
    private Byte status;

}
