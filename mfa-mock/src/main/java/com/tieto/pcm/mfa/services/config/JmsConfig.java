package com.tieto.pcm.mfa.services.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tieto.pcm.mfa.services.integration.jms.JmsSubsystem;
import com.tieto.pcm.mfa.services.integration.jms.helper.JmsIntegrationHelper;

@Configuration
public class JmsConfig {

    @Bean
    public JmsIntegrationHelper jmsIntegrationHelper(Environment env, GenericApplicationContext applicationContext) {
        return new JmsIntegrationHelper(env, applicationContext);
    }

    @Bean
    public JmsSubsystem jmsSubsystem(JmsIntegrationHelper jmsIntegrationHelper, ObjectMapper mapper) {
        return new JmsSubsystem(jmsIntegrationHelper, mapper);
    }
}
