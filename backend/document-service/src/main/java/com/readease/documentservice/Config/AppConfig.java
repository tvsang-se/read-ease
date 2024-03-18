package com.readease.documentservice.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Bean
    @Scope("singleton")
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("")
                .build();
    }
}
