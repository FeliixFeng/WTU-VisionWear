package com.wtu.client;

import com.wtu.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gaochen
 * @Date: 2025/09/22/12:34
 * @Description:
 */
@FeignClient("VisionWear-Image")
public interface ImageClient {

    @GetMapping("/api/image")
    Result<List<String>> getAllImageUrls(@RequestParam("userId") Long userId);
}
