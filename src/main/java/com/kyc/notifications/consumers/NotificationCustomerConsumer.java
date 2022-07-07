package com.kyc.notifications.consumers;

import com.kyc.core.model.web.RequestData;
import com.kyc.notifications.model.NotificationData;
import com.kyc.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationCustomerConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationCustomerConsumer.class);

    @Autowired
    private NotificationService notificationService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="kyc-queue-1",durable = "false"),
            exchange = @Exchange(type = "topic",name = "kyc.customers",durable = "false"),
            key = "kyc.customers.*")
    )
    public void receiverMessage(NotificationData notificationData,
                                @Header(name = "Authorization") String sender,
                                @Header(name = "kyc-customer-id-receiver") String receiver,
                                @Header(name = "channel") String channel){

        LOGGER.info("Received message from Rabbit MQ for saving a notification");

        Map<String,Object> params = new HashMap<>();
        params.put("sender",sender);
        params.put("receiver",receiver);
        params.put("channel",channel);

        RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                .pathParams(params)
                .body(notificationData)
                .build();
        notificationService.addNotification(req);
    }


}
