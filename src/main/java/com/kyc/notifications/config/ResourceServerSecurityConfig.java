package com.kyc.notifications.config;

import com.kyc.core.properties.KycMessages;
import com.kyc.core.security.jwt.BearerTokenAuthenticationEntryPointDelegate;
import com.kyc.core.security.jwt.KycUserSessionTokenJwtDecoder;
import com.kyc.core.security.jwt.KycUserTokenSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

import static com.kyc.notifications.constants.AppConstants.MESSAGE_004;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerSecurityConfig {

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private KycMessages kycMessages;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(CsrfConfigurer::disable);
        http.authorizeHttpRequests((authorize -> {
           authorize.requestMatchers("/actuator/**").permitAll()
                   .anyRequest().authenticated();
        }));
        http.formLogin(FormLoginConfigurer::disable);
        http.httpBasic(HttpBasicConfigurer::disable);
        http.oauth2ResourceServer(customizer -> customizer
                .jwt(Customizer.withDefaults())
                .authenticationEntryPoint(bearerTokenAuthenticationEntryPointDelegate()));
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(bearerTokenAuthenticationEntryPointDelegate()));
        return http.build();
    }

    @Bean
    public BearerTokenAuthenticationEntryPointDelegate bearerTokenAuthenticationEntryPointDelegate(){
        return new BearerTokenAuthenticationEntryPointDelegate(kycMessages.getMessage(MESSAGE_004),objectMapper);
    }

    @Bean
    public JwtDecoder jwtDecoder(KycUserTokenSessionService kycUserTokenSessionService){

        return new KycUserSessionTokenJwtDecoder(kycUserTokenSessionService);
    }
}
