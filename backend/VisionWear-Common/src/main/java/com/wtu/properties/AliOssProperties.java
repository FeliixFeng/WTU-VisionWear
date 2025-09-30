package com.wtu.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wtu.alioss")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AliOssProperties {

    // 阿里云OSS端点
    private String endpoint;
    // 阿里云OSS访问密钥ID
    private String accessKeyId;
    // 阿里云OSS访问密钥
    private String accessKeySecret;
    // 阿里云OSS存储bucket名称
    private String bucketName;

}
