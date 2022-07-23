package com.kyc.notifications.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@EnableRabbit
@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer,
                                                                                     ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConsumerTagStrategy(q -> "kyc-notifications."+q);

        //factory.setAfterReceivePostProcessors();

        configurer.configure(factory,connectionFactory);

        return factory;
    }

    @Bean
    public ConnectionNameStrategy defineConnectionNameStrategy() {
        return connectionFactory -> applicationName;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setValidator(validator);
    }
}
