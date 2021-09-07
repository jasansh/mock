package com.tieto.pcm.mfa.services.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tieto.pcm.core.srv.integration.jackson.ObjectMapperFactory;

@Configuration
public class HelperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return ObjectMapperFactory.createAndConfigure();
    }

    @Bean("cachedThreadPool")
    public ExecutorService cachedThreadPool() {
        return Executors.newCachedThreadPool();
    }
}
