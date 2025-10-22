package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @description: 修改用户信息DTO
 */
@Data
public class ChangeInfoDTO {
    @Schema(description = "用户名")
    @Size(max = 20, message = "用户名长度不能超过20个字符")
    private String userName;
    
    @Schema(description = "用户邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "用户手机号码")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户头像URL")
    private String avatar;

    @Schema(description = "用户生日")
    private String birthday;

} 