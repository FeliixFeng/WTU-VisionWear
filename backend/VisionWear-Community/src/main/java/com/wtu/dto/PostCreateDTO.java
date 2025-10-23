package com.wtu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建帖子DTO
 *
 * @author WTU
 */
@Data
@Schema(description = "创建帖子DTO")
public class PostCreateDTO {

    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Schema(description = "帖子标题")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "封面图片URL")
    private String coverImage;

    @NotNull(message = "帖子类型不能为空")
    @Schema(description = "帖子类型：1-图文 2-纯文字 3-图片分享")
    private Integer postType;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "标签ID列表")
    private List<Long> tagIds;
}

