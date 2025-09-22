package com.wtu.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS工具类
 */
@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    // 阿里云OSS端点
    private String endpoint;

    // 阿里云OSS访问密钥ID
    private String accessKeyId;

    // 阿里云OSS访问密钥
    private String accessKeySecret;

    // 阿里云OSS存储bucket名称
    private String bucketName;

    /**
     * 文件上传
     *
     * @param bytes 文件字节数组
     * @param objectName 对象名称
     * @return 访问URL
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建PutObject请求
            ossClient.putObject(bucketName,
                    objectName,
                    new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            throw new RuntimeException("OSS上传文件失败: " + oe.getErrorMessage());
        } catch (ClientException ce) {
            throw new RuntimeException("OSS客户端异常: " + ce.getMessage());
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return getAccessUrl(objectName);
    }

    /**
     * 获取OSS对象的访问URL
     *
     * @param objectName 对象名称
     * @return 访问URL
     */
    public String getAccessUrl(String objectName) {
        // 文件访问路径规则 https://BucketName.Endpoint/ObjectName
        StringBuilder stringBuilder = new StringBuilder("https://");

        // 拼接URL
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);

        return stringBuilder.toString();
    }

    /**
     * 删除OSS对象
     *
     * @param objectName 对象名称
     */
    public void delete(String objectName) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除对象
            ossClient.deleteObject(bucketName, objectName);
        } catch (OSSException oe) {
            throw new RuntimeException("OSS删除文件失败: " + oe.getErrorMessage());
        } catch (ClientException ce) {
            throw new RuntimeException("OSS客户端异常: " + ce.getMessage());
        } finally {
            // 关闭OSSClient
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}