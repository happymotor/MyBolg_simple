package com.myblog.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryQueryVO {
    private Long  categoryId;

    private String categoryName;

    private Byte status;

    private LocalDateTime createTime;

    private Integer articleCount;
}
