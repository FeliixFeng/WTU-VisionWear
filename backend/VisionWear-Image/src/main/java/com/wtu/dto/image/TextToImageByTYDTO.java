package com.wtu.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通义万象文生图请求参数封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextToImageByTYDTO {


    @Schema(description = "模型名称，固定为wanx2.1-t2i-turbo",defaultValue = "wanx2.1-t2i-turbo")
    @NotBlank(message = "model为必填项")
    private String model = "wanx2.1-t2i-turbo" ;


    @Schema(description = "生成图像的提示词", required = true)
    @NotBlank(message = "prompt为必填项")
    private String prompt;

    @Schema(description = "负面提示词，非必填")
    private String negative_prompt;

    @Schema(description = "图像尺寸，格式如1024*1024", example = "1024*1024")
    private String size = "1024*1024";

    @Schema(description = "生成图像数量", example = "1")
    @Min(value = 1, message = "n不能小于1")
    private Integer n = 1;

    @Schema(description = "随机种子", example = "42")
    private Integer seed = -1;

    @Schema(description = "是否开启提示词扩写", example = "true")
    @JsonProperty("prompt_extend")
    private Boolean promptExtend = true;

    @Schema(description = "是否添加水印", example = "false")
    private Boolean watermark = false;

}