package com.tieto.pcm.mfa.services.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource(value = {
    "classpath:mfa-mock/env-config.properties",
})
@Import({JmsConfig.class, HelperConfig.class})
@ComponentScan({"com.tieto.pcm.mfa.services"})
@EnableTransactionManagement
public class ServiceConfig {
}
