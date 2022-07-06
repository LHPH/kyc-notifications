package com.kyc.notifications.service;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.web.MessageData;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.notifications.model.NotificationData;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.kyc.notifications.constants.AppConstants.MESSAGE_002;
import static com.kyc.notifications.constants.AppConstants.MESSAGE_003;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private RedisTemplate<String,NotificationData> redisTemplate;

    @Autowired
    private KycMessages kycMessages;

    @Value("${kyc-config.number-notifications:10}")
    private int numberNotificationsByUser;

    public ResponseData<Void> addNotification(RequestData<NotificationData> req) {

        try{
            Map<String,Object> params = req.getPathParams();
            String clientNumber = params.get("receiver").toString();
            LOGGER.info("Processing request to save the notification for {}",clientNumber);

            NotificationData notificationData = req.getBody();
            notificationData.setDate(new Date());

            Long notificationCount = getNotificationsCountByCustomer(clientNumber);
            notificationCount++;

            LOGGER.info("Checking the number of notifications that {} has",clientNumber);
            if(notificationCount<=numberNotificationsByUser){

                LOGGER.info("Saving the new notification in redis for {}",clientNumber);
                redisTemplate.opsForList().leftPush(clientNumber,notificationData);
                return ResponseData.of(null);
            }

            LOGGER.warn("The {} exceed the limit of notifications for user",clientNumber);
            MessageData messageData = kycMessages.getMessage(MESSAGE_002);
            throw KycRestException.builderRestException()
                    .inputData(req)
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorData(messageData)
                    .build();

        }
        catch (DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MESSAGE_003);
            throw KycRestException.builderRestException()
                    .inputData(req)
                    .exception(ex)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .errorData(messageData)
                    .build();
        }
    }

    public ResponseData<List<NotificationData>> getNotifications(RequestData<Void> req){

        try{
            Map<String,Object> params = req.getPathParams();
            String clientNumber = params.get("client").toString();

            LOGGER.info("Retrieving the notifications for {}",clientNumber);
            List<NotificationData> notifications = new ArrayList<>();

            Long notificationsCount = getNotificationsCountByCustomer(clientNumber);
            LOGGER.info("The customer {} has {} notifications in redis",clientNumber,notificationsCount);
            if(notificationsCount>0){

                notifications = redisTemplate.opsForList().leftPop(clientNumber,notificationsCount);
            }
            LOGGER.info("Returning the found notifications for {}",clientNumber);
            return ResponseData.of(notifications);
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MESSAGE_003);
            throw KycRestException.builderRestException()
                    .inputData(req)
                    .exception(ex)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .errorData(messageData)
                    .build();
        }
    }

    public Long getNotificationsCountByCustomer(String clientNumber){

        try{
            LOGGER.info("Getting the number of notifications for {}",clientNumber);
            return ObjectUtils.defaultIfNull(redisTemplate.opsForList().size(clientNumber),0L);
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MESSAGE_003);
            throw KycRestException.builderRestException()
                    .inputData(clientNumber)
                    .exception(ex)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .errorData(messageData)
                    .build();
        }
    }

}
