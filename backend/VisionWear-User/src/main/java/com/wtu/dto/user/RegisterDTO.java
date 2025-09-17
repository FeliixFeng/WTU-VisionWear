package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "注册请求参数")
public class RegisterDTO {
    @Schema(description = "用户名", example = "username")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "password")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱", example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}

