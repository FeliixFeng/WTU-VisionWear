package com.wtu.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * 通义万象文生图请求
 */
@Data
@Builder
public class TextToImageByTYVO {
    private String requestId;
    private List<GeneratedImage> images;
    private String prompt;
    private long generationTimeMs;

    @Data
    @Builder
    public static class GeneratedImage {

        private String imageId;
        // 可以移除base64Image字段，减少传输数据量
        // private String base64Image;
        private String imageUrl;
        private int width;
        private int height;
        private long seed;
    }
}