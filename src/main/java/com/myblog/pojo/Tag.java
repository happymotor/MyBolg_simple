package com.myblog.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.myblog.Common.RegexPatternsConstants;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_tag")
public class Tag {

    //标签不需要status和逻辑删除

    @TableId(type = IdType.AUTO)
    private Long tagId;

    @Pattern(regexp = RegexPatternsConstants.TAG_NAME_REGEX,message = "标签长度应为1-10")
    private String tagName;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}
