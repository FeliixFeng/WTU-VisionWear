package com.wtu.client;

import com.wtu.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/09/22/12:34
 * @Description: Image服务Feign客户端
 */
@FeignClient("VisionWear-Image")
public interface ImageClient {

    /**
     * 获取用户的所有图片URL
     * @param userId 用户ID（通过请求头传递）
     * @return 图片URL列表
     */
    @GetMapping("/api/image")
    Result<List<String>> getAllImageUrls(@RequestHeader(value = "userId") Long userId);
}
