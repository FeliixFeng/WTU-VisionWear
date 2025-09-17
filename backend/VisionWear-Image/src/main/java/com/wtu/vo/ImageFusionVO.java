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
    private String jobId;  // 新增字段
    private List<GeneratedImage> images; // 可为空
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
