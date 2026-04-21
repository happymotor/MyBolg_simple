package com.myblog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_article_tag")
public class ArticleTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long tagId;

    private LocalDateTime createTime;

}
