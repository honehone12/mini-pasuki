package com.mini.pasuki.configs;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class SignatureConfig {
    @PostConstruct
    public void setupProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
