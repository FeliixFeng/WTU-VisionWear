package com.wtu.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通义万象线稿成图请求参数封装类（使用wanx2.1-imageedit模型）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SketchToImageByTYDTO {

    @Schema(description = "模型名称，固定为wanx2.1-imageedit", defaultValue = "wanx2.1-imageedit")
    @NotBlank(message = "model为必填项")
    private String model = "wanx2.1-imageedit";

    @Schema(description = "生成图像的提示词")
    @NotBlank(message = "prompt为必填项")
    private String prompt;

    @Schema(description = "基础图像URL（线稿图），必填")
    @NotBlank(message = "baseImageUrl为必填项")
    @JsonProperty("base_image_url")
    private String baseImageUrl;

    @Schema(description = "生成图像数量", example = "1")
    @Min(value = 1, message = "n不能小于1")
    private Integer n = 1;

    @Schema(description = "图像尺寸，格式如1024*1024", example = "1024*1024")
    private String size = "1024*1024";

    @Schema(description = "是否为线稿，false表示为线稿，ture表示为简单涂鸦", example = "false")
    @JsonProperty("is_sketch")
    private Boolean isSketch = false;

    @Schema(description = "是否开启提示词扩写", example = "true")
    @JsonProperty("prompt_extend")
    private Boolean promptExtend = true;

    @Schema(description = "随机种子，非必填")
    private Integer seed;
}

