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

    @Schema(description = "Access Token（短期令牌，15分钟）")
    private String accessToken;

    @Schema(description = "Refresh Token（长期令牌，7天）")
    private String refreshToken;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "用户ID")
    private Long userId;
}
