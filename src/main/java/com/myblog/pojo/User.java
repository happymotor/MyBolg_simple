package com.myblog.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.myblog.Utils.RegexPatternsUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer userId;//用户id 主键

    @Pattern(regexp = RegexPatternsUtils.USERNAME_REGEX,
            message = "长度应为3~20位的字母、数字或者下划线")
    private String username;//用户名称

    @Pattern(regexp = RegexPatternsUtils.PASSWORD_REGEX)
    private  String password;//用户密码


    private String nickname;//昵称
    @Email
    private String email;//邮箱
    private Byte status;//状态 0-禁用 1-启用
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    @TableField("is_deleted")
    private Boolean isDeleted;//用户是否被删除 0-未删除 1-删除

}
