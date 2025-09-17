package com.wtu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动时打印Swagger文档地址
 */
@Component
public class SwaggerPrintConfig implements ApplicationRunner {

    @Value("${server.port:9191}")
    private String serverPort;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("\n----------------------------------------------------------");
        System.out.println("API文档已启动，访问地址：");
        System.out.println("Knife4j UI: http://localhost:" + serverPort + "/doc.html");
        System.out.println("----------------------------------------------------------\n");
    }
} 