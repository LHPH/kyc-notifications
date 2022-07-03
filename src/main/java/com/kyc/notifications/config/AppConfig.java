package com.kyc.notifications.config;

import com.kyc.core.config.RedisConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {RedisConfig.class})
public class AppConfig {
}
