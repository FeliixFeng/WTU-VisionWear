package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "图片信息表")
public class Image {
    @TableId(type = IdType.ASSIGN_UUID) // 使用UUID作为主键
    @Schema(description = "图片唯一ID")
    private String imageId;

    @Schema(description = "关联用户ID")
    private Long userId;

    @Schema(description = "图片OSS访问URL")
    private String imageUrl;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "图片状态（0-正常，1-已删除）")
    private Integer status;
}