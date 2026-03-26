package com.myblog.Dto;


import com.myblog.Utils.RegexPatternsUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDto {

    @NotEmpty
    @Pattern(regexp = RegexPatternsUtils.USERNAME_REGEX,
            message = "长度应为3~20位的字母、数字或者下划线")
    private String username;

    @NotEmpty
    @Pattern(regexp = RegexPatternsUtils.PASSWORD_REGEX,
            message = "密码长度应为6~32位")
    private String password;

    @NotEmpty
    @Pattern(regexp = RegexPatternsUtils.USERNAME_REGEX)
    private String confirmPassword;

    @Pattern(regexp = RegexPatternsUtils.NICKNAME_REGEX,
    message = "昵称长度应为1~20位")
    private String nickname;

    @NotEmpty
    @Email(message = "不符合邮件格式")
    private String email;
}
