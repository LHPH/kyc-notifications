package com.kyc.notifications.service;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.MessageData;
import com.kyc.core.model.jwt.JwtData;
import com.kyc.core.model.notifications.NotificationData;
import com.kyc.core.model.notifications.NotificationDetail;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.core.util.TokenUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kyc.core.constants.GeneralConstants.ID_RECIPIENT;
import static com.kyc.notifications.constants.AppConstants.KEY_PREFIX;
import static com.kyc.notifications.constants.AppConstants.MESSAGE_002;
import static com.kyc.notifications.constants.AppConstants.MESSAGE_003;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private RedisTemplate<String,NotificationDetail> redisTemplate;

    @Autowired
    private KycMessages kycMessages;

    @Value("${kyc-config.number-notifications:10}")
    private int numberNotificationsByUser;

    public ResponseData<Void> addNotification(RequestData<NotificationData> req) {

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        JwtData jwtData = TokenUtil.transform(jwtAuthenticationToken.getToken());

        Map<String,Object> headers = req.getHeaders();
        String idRecipient = headers.get(ID_RECIPIENT).toString();
        return addNotification(req.getBody(),String.valueOf(jwtData.getUser()), idRecipient,jwtData.getChannel());
    }

    public ResponseData<Void> addNotification(NotificationData notificationData, String idIssuer,
                                              String idRecipient, String channel){

        try{
            LOGGER.info("Processing request to save the notification for {}",idRecipient);

            NotificationDetail notificationDetail = new NotificationDetail(notificationData);
            notificationDetail.setIssuer(idIssuer);
            notificationDetail.setRecipient(idRecipient);
            notificationDetail.setChannel(channel);

            Long notificationCount = getNotificationsCountByCustomer(idRecipient);
            notificationCount++;

            LOGGER.info("Checking the number of notifications that {} has",idRecipient);
            if(notificationCount<=numberNotificationsByUser){

                LOGGER.info("Saving the new notification in redis for {}",idRecipient);
                redisTemplate.opsForList().leftPush(generateKey(idRecipient),notificationDetail);
                return ResponseData.emptyResponse();
            }

            LOGGER.warn("The {} exceed the limit of notifications for user",idRecipient);
            MessageData messageData = kycMessages.getMessage(MESSAGE_002);
            throw KycRestException.builderRestException()
                    .inputData(notificationData)
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorData(messageData)
                    .build();
        }
        catch (DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MESSAGE_003);
            throw KycRestException.builderRestException()
                    .inputData(notificationData)
                    .exception(ex)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .errorData(messageData)
                    .build();
        }
    }

    public ResponseData<List<NotificationDetail>> getNotifications(RequestData<Void> req){

        try{
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            JwtData jwtData = TokenUtil.transform(jwtAuthenticationToken.getToken());

            String userId = String.valueOf(jwtData.getUser());

            LOGGER.info("Retrieving the notifications for {}",userId);
            List<NotificationDetail> notifications = new ArrayList<>();

            Long notificationsCount = getNotificationsCountByCustomer(userId);
            LOGGER.info("The customer {} has {} notifications in redis",userId,notificationsCount);
            if(notificationsCount>0){

                notifications = redisTemplate.opsForList().leftPop(generateKey(userId),notificationsCount);
            }
            LOGGER.info("Returning the found notifications for {}",userId);
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

    public Long getNotificationsCountByCustomer(String userIdRecipient){

        try{
            LOGGER.info("Getting the number of notifications for {}",userIdRecipient);
            return ObjectUtils.defaultIfNull(redisTemplate.opsForList().size(generateKey(userIdRecipient)),0L);
        }
        catch(DataAccessException ex){

            MessageData messageData = kycMessages.getMessage(MESSAGE_003);
            throw KycRestException.builderRestException()
                    .inputData(userIdRecipient)
                    .exception(ex)
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .errorData(messageData)
                    .build();
        }
    }

    private String generateKey(String idRecipient){
        return KEY_PREFIX + idRecipient;
    }

}
