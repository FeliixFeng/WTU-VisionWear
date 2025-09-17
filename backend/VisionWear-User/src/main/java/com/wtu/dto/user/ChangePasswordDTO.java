package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author gaochen
 * @description: 修改密码DTO
 * @date 2025/7/4 09:40
 */
@Data
public class ChangePasswordDTO {
    @Schema(description = "用户名", example = "username")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "旧密码", example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "123456789")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
