package com.wtu.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger 资源控制器
 * 为 Knife4j 网关聚合提供 swagger-resources 端点
 * 
 * @author WTU
 */
@RestController
public class SwaggerResourceController {

    /**
     * 提供 swagger-resources 端点
     * Knife4j 前端会请求这个端点来获取所有微服务的文档列表
     */
    @GetMapping("/swagger-resources")
    public List<SwaggerResourceDTO> swaggerResources() {
        List<SwaggerResourceDTO> resources = new ArrayList<>();
        
        // 认证模块
        resources.add(new SwaggerResourceDTO("认证模块", "/v3/api-docs/auth", "3.0"));
        
        // 用户模块
        resources.add(new SwaggerResourceDTO("用户模块", "/v3/api-docs/user", "3.0"));
        
        // 图片模块
        resources.add(new SwaggerResourceDTO("图片模块", "/v3/api-docs/image", "3.0"));
        
        // 社区模块
        resources.add(new SwaggerResourceDTO("社区模块", "/v3/api-docs/community", "3.0"));
        
        return resources;
    }

    /**
     * UI 配置端点（可选，返回空对象即可）
     */
    @GetMapping("/swagger-resources/configuration/ui")
    public Object uiConfiguration() {
        return new Object();
    }

    /**
     * 安全配置端点（可选，返回空对象即可）
     */
    @GetMapping("/swagger-resources/configuration/security")
    public Object securityConfiguration() {
        return new Object();
    }

    /**
     * Swagger 资源 DTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SwaggerResourceDTO {
        /**
         * 资源名称（显示在文档界面的模块名）
         */
        private String name;
        
        /**
         * 资源 URL（OpenAPI 文档的路径）
         */
        private String url;
        
        /**
         * Swagger 版本
         */
        private String swaggerVersion;
    }
}

