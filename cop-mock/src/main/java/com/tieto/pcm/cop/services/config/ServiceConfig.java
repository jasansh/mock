package com.tieto.pcm.cop.services.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({HelperConfig.class})
@ComponentScan({"com.tieto.pcm.cop.services"})
@EnableTransactionManagement
public class ServiceConfig {
}
