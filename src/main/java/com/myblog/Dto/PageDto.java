package com.myblog.Dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {
    //页码
    @NotNull
    @Min(value = 1)
    private Long pageNum=1L;

    //每页条数
    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    private Long pageSize=10L;

    //模糊搜索关键词
    private  String keyword;




}
