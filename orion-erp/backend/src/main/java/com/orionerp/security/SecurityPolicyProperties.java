package com.orionerp.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityPolicyProperties {

    private int maxLoginAttempts = 5;
    private int lockMinutes = 15;
}
