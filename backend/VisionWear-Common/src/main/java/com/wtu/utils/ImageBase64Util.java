package com.wtu.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Component
public class ImageBase64Util {

    /**
     * 从图片URL读取图片并转成Base64字符串
     * @param imageUrl 图片公网地址
     * @return 带data:image/png;base64,前缀的Base64字符串
     */
    public String imageUrlToBase64(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            try (InputStream inputStream = conn.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                byte[] imageBytes = outputStream.toByteArray();
                String base64 = Base64Utils.encodeToString(imageBytes);
                return "data:image/png;base64," + base64;
            }
        } catch (Exception e) {
            log.error("图片URL转Base64失败: {}", imageUrl, e);
            throw new RuntimeException("图片URL转Base64失败", e);
        }
    }
}
