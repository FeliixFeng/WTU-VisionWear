package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "用户类型实体")
public class User implements Serializable {
    @TableId(value = "user_id", type = IdType.AUTO)
    @Schema(description = "用户唯一性id")
    private Long userId;

    @Schema(description = "用户账户名")
    private String userName;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "生日日期")
    private LocalDate birthday;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "用户加密后密码")
    private String passWord;

    @Schema(description = "用户注册邮箱")
    private String email;

    @Schema(description = "用户手机号码")
    private String phone;

    @Schema(description = "用户角色")//0-普通用户，1-VIP，2-管理员
    private Integer role;

    @Schema(description = "用户账户状态")//0-正常 1-封禁 2-待激活
    private Integer status;

    @Schema(description = "账户注册时间")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "账户注最后登录时间")@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    @Schema(description = "用户midjourney密钥")
    private String apiKey;

}
