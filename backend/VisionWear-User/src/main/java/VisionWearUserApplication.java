package com.wtu.visionwearuser;

import com.wtu.client.ImageClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = {ImageClient.class})
public class VisionWearUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisionWearUserApplication.class, args);
    }
}