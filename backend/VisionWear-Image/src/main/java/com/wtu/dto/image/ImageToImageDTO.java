package com.wtu.dto.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageToImageDTO {
    @Schema(description = "服务标识，固定为i2i_portrait_photo", defaultValue = "i2i_portrait_photo")
    @NotBlank(message = "reqKey为必填项")
    @JsonProperty("req_key")
    private String reqKey = "i2i_portrait_photo";

    @Schema(description = "图片URL", example = "https://图片1.png")
    @NotBlank(message = "imageInput为必填项")
    @JsonProperty("image_input")
    private String imageInput;

    @Schema(description = "生图提示词", defaultValue = "演唱会现场的合照，闪光灯拍摄")
    private String prompt;

    @Schema(description = "生成图像的宽，默认1328，范围[512,2048]", defaultValue = "1328")
    @Min(value = 512, message = "width不能小于512")
    @Max(value = 2048, message = "width不能大于2048")
    private Integer width = 1328;

    @Schema(description = "生成图像的高，默认1328，范围[512,2048]", defaultValue = "1328")
    @Min(value = 512, message = "height不能小于512")
    @Max(value = 2048, message = "height不能大于2048")
    private Integer height = 1328;

    @Schema(description = "高清处理效果，越高人脸清晰度越高，建议使用默认值", defaultValue = "0.4")
    @DecimalMin(value = "0.0", message = "gpen不能小于0")
    @DecimalMax(value = "1.0", message = "gpen不能大于1")
    private Float gpen = 0.4f;

    @Schema(description = "人脸美化效果，越高美颜效果越明显，建议使用默认值", defaultValue = "0.3")
    @DecimalMin(value = "0.0", message = "skin不能小于0")
    @DecimalMax(value = "1.0", message = "skin不能大于1")
    private Float skin = 0.3f;

    @Schema(description = "匀肤效果，越高均匀肤色去除瑕疵效果越明显，建议使用默认值", defaultValue = "0")
    @DecimalMin(value = "0.0", message = "skinUnifi不能小于0")
    @DecimalMax(value = "1.0", message = "skinUnifi不能大于1")
    @JsonProperty("skin_unifi")
    private Float skinUnifi = 0f;

    @Schema(description = "参考模式，可选值：creative(提示词模式)、reference(全参考模式)、reference_char(人物参考模式)")
    @JsonProperty("gen_mode")
    private String genMode;

    @Schema(description = "随机种子，作为确定扩散初始状态的基础，默认-1（随机）", defaultValue = "-1")
    private String seed = "-1";
}