package com.wtu.service.impl;

import com.wtu.entity.Image;
import com.wtu.exception.BusinessException;
import com.wtu.exception.ExceptionUtils;
import com.wtu.mapper.ImageMapper;
import com.wtu.service.ImageStorageService;
import com.wtu.utils.AliOssUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.net.URL;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {

    // IOC 注入
    private final AliOssUtil aliOssUtil;
    private final ImageMapper imageMapper;

    @Override
    public String saveBase64Image(String base64Image, Long userId) {
        ExceptionUtils.requireNonEmpty(base64Image, "图像数据不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            String imageId = UUID.randomUUID().toString();
            String objectName = imageId + ".png";

            // 解码并上传到OSS
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            String imageUrl = aliOssUtil.upload(imageBytes, objectName);

            // 插入数据库记录
            Image image = Image.builder()
                    .imageId(imageId)
                    .userId(userId)
                    .imageUrl(imageUrl)
                    .createTime(LocalDateTime.now())
                    .status(0)
                    .build();
            imageMapper.insert(image);

            return imageId;

        } catch (IllegalArgumentException e) {
            throw new BusinessException("图像格式不正确，无法解码");
        } catch (Exception e) {
            throw new BusinessException("保存图像失败: " + e.getMessage());
        }
    }

    @Override
    public String saveImageFromUrl(String imageUrl, Long userId) {
        ExceptionUtils.requireNonEmpty(imageUrl, "图像URL不能为空");
        ExceptionUtils.requireNonNull(userId, "用户ID不能为空");
        
        try {
            String imageId = UUID.randomUUID().toString();
            String objectName = imageId + ".png";

            // 1. 从URL下载图片数据
            byte[] imageBytes;

            try (InputStream in = new URL(imageUrl).openStream()) {
                imageBytes = in.readAllBytes();
            }

            // 2. 上传到OSS
            String ossImageUrl = aliOssUtil.upload(imageBytes, objectName);

            // 3. 插入数据库记录
            Image image = Image.builder()
                    .imageId(imageId)
                    .userId(userId)
                    .imageUrl(ossImageUrl)
                    .createTime(LocalDateTime.now())
                    .status(0)
                    .build();
            imageMapper.insert(image);

            return imageId;
        } catch (IOException e) {
            throw new BusinessException("无法从URL获取图片: " + e.getMessage());
        } catch (Exception e) {
            throw new BusinessException("保存图片失败: " + e.getMessage());
        }
    }
    
    @Override
    @Cacheable(value = "imageUrls", key = "#imageId", unless = "#result == null")
    public String getImageUrl(String imageId) {
        ExceptionUtils.requireNonEmpty(imageId, "图像ID不能为空");
        if(imageId.endsWith(".png")){
            return  aliOssUtil.getAccessUrl(imageId);
        }
        // 直接构建并返回URL
        String objectName = imageId + ".png";
        return aliOssUtil.getAccessUrl(objectName);
    }
}