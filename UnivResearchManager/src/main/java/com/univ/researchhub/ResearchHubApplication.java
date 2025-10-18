package com.univ.researchhub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.univ.researchhub.mapper") // 扫描 mapper 包下的所有接口
public class ResearchHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResearchHubApplication.class, args);
    }
}