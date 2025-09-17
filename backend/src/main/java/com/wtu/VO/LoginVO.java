package com.wtu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录响应信息")
public class LoginVO {

    @Schema(description = "JWT 令牌")
    private String token;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户ID")
    private Long userId;
}
