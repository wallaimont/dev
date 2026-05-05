package com.orionerp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupBannerConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("OrionERP API pronta - Swagger: /swagger-ui.html");
    }
}
