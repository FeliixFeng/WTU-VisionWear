package com.wtu.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.File;

public class OssImageUploader {

    /**
     * 上传本地图片到阿里云OSS，并返回图片的访问URL
     * @param localFilePath 本地图片路径，如 "C:/Users/Lenovo/Desktop/1.jpg"
     * @param objectName OSS对象名，如 "images/xxx.jpg"
     * @param bucketName OSS Bucket名称
     * @param endpoint OSS服务Endpoint
     * @param accessKeyId 阿里云AccessKeyId
     * @param accessKeySecret 阿里云AccessKeySecret
     * @return 图片在OSS上的访问URL
     */
    public String uploadLocalImageToOss(String localFilePath, String objectName,
                                        String bucketName, String endpoint,
                                        String accessKeyId, String accessKeySecret) {
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, objectName, new File(localFilePath));
            // 拼接访问URL（如需自定义域名或签名URL可按需调整）
            return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectName;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
