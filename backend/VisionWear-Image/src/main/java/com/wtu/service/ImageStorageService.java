package com.wtu.service;

import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/04/13/21:19
 * @Description: 图像缓存接口
 */
public interface ImageStorageService {

    /**
     * 获取图像URL
     *
     * @param imageId 图像ID
     * @return 图像URL
     */
    String getImageUrl(String imageId);

    /**
     * 保存Base64编码的图像到阿里云OSS
     *
     * @param base64Image Base64编码的图像数据
     * @param userId 用户ID
     * @return 生成的图像ID
     */
    String saveBase64Image(String base64Image, Long userId); // 添加 userId 参数


    /**
     * 从URL下载图像并保存到阿里云OSS
     *
     * @param imageUrl 图像URL
     * @param userId 用户ID
     * @return 生成的图像ID
     */
    String saveImageFromUrl(String imageUrl, Long userId);
}
