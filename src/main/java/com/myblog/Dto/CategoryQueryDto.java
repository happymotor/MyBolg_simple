package com.myblog.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryQueryDto {

    private String keyword;
    //栏目状态：1-启用，0-禁用
    private Byte status;

}
