package com.kyc.notifications.handlers;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.listener.ListenerExecutionFailedException;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class LogRabbitErrorHandler implements RabbitListenerErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRabbitErrorHandler.class);

    @Override
    public Object handleError(Message message, Channel channel, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {

        LOGGER.warn("In handle error {}",message.getMessageProperties());
        throw e;
    }
}
