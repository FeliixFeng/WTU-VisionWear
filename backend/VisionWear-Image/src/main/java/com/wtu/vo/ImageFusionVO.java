package com.wtu.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageFusionVO {
    private String requestId;
    private String jobId;  // 任务ID
    private List<GeneratedImage> images; // 生成的图片列表（未完成时为空）
    private Integer progress; // 任务进度 0-100
    private long generationTimeMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedImage {
        private String imageId;
        private String imageUrl;
    }
}
