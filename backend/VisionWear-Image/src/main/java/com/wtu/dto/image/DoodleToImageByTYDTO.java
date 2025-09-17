package com.wtu.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通义万象涂鸦作图请求参数封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoodleToImageByTYDTO {

    @Schema(description = "模型名称，固定为wanx-sketch-to-image-lite",defaultValue = "wanx-sketch-to-image-lite")
    @NotBlank(message = "model为必填项")
    private String model = "wanx-sketch-to-image-lite";

    @Schema(description = "生成图像的提示词", required = true)
    @NotBlank(message = "prompt为必填项")
    private String prompt;

    @Schema(description = "涂鸦图像URL，必填")
    @NotBlank(message = "sketchImageUrl为必填项")
    @JsonProperty("sketch_image_url")
    private String sketchImageUrl;

    @Schema(description = "生成图像数量", example = "1")
    @Min(value = 1, message = "n不能小于1")
    private Integer n = 1;

    @Schema(description = "图像尺寸，格式如768*768", example = "768*768")
    private String size = "768*768";

    @Schema(description = "风格标签，如'<3d cartoon>', '<anime>', '<oil painting>', '<watercolor>', '<sketch>', '<chinese painting>', '<flat illustration>', '<auto>'，非必填",example = "<auto>")
    private String style = "<auto>";

    @Schema(description = "随机种子，非必填")
    private Integer seed;

    @Schema(description = "是否添加水印", example = "false")
    private Boolean watermark = false;
}
