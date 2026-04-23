package com.myblog.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticlePageDto extends PageDto{

    private Long categoryId;


    @NotNull
    //0-草稿，1-已发表，2-回收站
    private Byte status;

    //默认当前登录用户
    private Long userId;

}
