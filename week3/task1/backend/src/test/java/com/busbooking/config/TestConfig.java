package com.busbooking.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import com.busbooking.security.JwtTokenProvider;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        JwtTokenProvider provider = new JwtTokenProvider();
        provider.setJwtSecret("testSecretKey1234567890123456789012345678901234567890");
        provider.setJwtExpirationInMs(3600000); // 1 hour
        return provider;
    }
} 