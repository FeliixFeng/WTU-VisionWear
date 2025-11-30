package com.wtu.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Neo4j数据库配置类
 */
@Configuration
public class Neo4jConfig {

    @Value("${vision.neo4j.uri}")
    private String uri;

    @Value("${vision.neo4j.user}")
    private String user;

    @Value("${vision.neo4j.password}")
    private String password;

    @Bean
    public Driver neo4jDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
}

