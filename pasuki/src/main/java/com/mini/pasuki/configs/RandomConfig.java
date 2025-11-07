package com.mini.pasuki.configs;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomConfig {
    @Bean
    SecureRandom getRandom() {
        return new SecureRandom();
    }
}
