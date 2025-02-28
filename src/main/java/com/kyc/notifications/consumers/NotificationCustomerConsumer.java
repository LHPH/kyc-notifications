package com.kyc.notifications.consumers;

import com.kyc.core.model.notifications.NotificationData;
import com.kyc.notifications.service.NotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.kyc.core.constants.GeneralConstants.CHANNEL;
import static com.kyc.core.constants.GeneralConstants.ID_ISSUER;
import static com.kyc.core.constants.GeneralConstants.ID_RECIPIENT;


@Component
public class NotificationCustomerConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationCustomerConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="kyc-queue-1",durable = "true"),
            exchange = @Exchange(type = "topic",name = "kyc.customers",durable = "true"),
            key = "kyc.customers.*"),errorHandler = "logRabbitErrorHandler", returnExceptions = "true"
    )
    public void receiverMessage(@Payload @Valid NotificationData notificationData,
                                @Header(name = ID_ISSUER) String issuer,
                                @Header(name = ID_RECIPIENT) String recipient,
                                @Header(name = CHANNEL) String channel){

        LOGGER.info("Received message from Rabbit MQ for saving a notification");
        notificationService.addNotification(notificationData,issuer,recipient,channel);
    }


}
