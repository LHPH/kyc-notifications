package com.kyc.notifications.consumers;

import com.kyc.notifications.model.NotificationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class CustomerConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerConsumer.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="kyc-queue-1",durable = "false"),
            exchange = @Exchange(type = "topic",name = "kyc.customers",durable = "false"),
            key = "kyc.customers.*")
    )
    public void receiverMessage(NotificationData notificationData, @Header(name = "x-sender") String header){

        LOGGER.info("Received message {} {}",notificationData,header);
    }


}
