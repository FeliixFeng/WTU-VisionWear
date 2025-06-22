package com.wtu.controller;

import com.wtu.result.Result;
import com.wtu.service.ImageService;
import com.wtu.service.MaterialService;
import com.wtu.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "用户模块")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    // IOC 注入
    private final ImageService imageService;

    private final MaterialService materialService;

    @GetMapping(value = "/getAllImage")
    @Operation(summary = "获取用户生成的所有图片URL")
    public Result<List<String>> getAllImage(HttpServletRequest httpServletRequest) {
        //从token 获取当前用户ID
        Long userId = UserContext.getCurrentUserId(httpServletRequest);

        // 获取用户生成的所有图片URL
        List<String> imageUrls = imageService.getAllImageUrls(userId);

        log.info("获取用户生成的所有图片URL: {}", imageUrls);

        return Result.success(imageUrls);
    }

    @GetMapping("/getMaterial")
    @Operation(summary = "获取素材库的图片")
    public Result<List<String>> getMaterial(){
        return Result.success(materialService.getMaterial());
    }

}