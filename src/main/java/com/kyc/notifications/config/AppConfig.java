package com.kyc.notifications.config;

import com.kyc.core.config.BuildDetailConfig;
import com.kyc.core.config.RedisConfig;
import com.kyc.core.exception.handlers.KycUnhandledExceptionHandler;
import com.kyc.core.exception.handlers.KycValidationRestExceptionHandler;
import com.kyc.core.properties.KycMessages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static com.kyc.notifications.constants.AppConstants.MESSAGE_000;
import static com.kyc.notifications.constants.AppConstants.MESSAGE_001;

@Configuration
@Import(value = {RedisConfig.class, KycMessages.class, BuildDetailConfig.class})
public class AppConfig {

    @Bean
    public KycUnhandledExceptionHandler kycUnhandledExceptionHandler(KycMessages kycMessages){

        return new KycUnhandledExceptionHandler(kycMessages.getMessage(MESSAGE_000));
    }

    @Bean
    public KycValidationRestExceptionHandler kycValidationRestExceptionHandler(KycMessages kycMessages){

        return new KycValidationRestExceptionHandler(kycMessages.getMessage(MESSAGE_001));
    }
}
