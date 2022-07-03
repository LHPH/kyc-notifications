package com.kyc.notifications.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.util.GeneralUtil;
import com.kyc.notifications.model.NotificationData;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private RedisTemplate<String,NotificationData> redisTemplate;

    public ResponseData<Void> addNotification(RequestData<NotificationData> req) {

        try{

            Map<String,Object> params = req.getPathParams();
            String clientNumber = params.get("receiver").toString();

            NotificationData notificationData = req.getBody();
            notificationData.setDate(new Date());

            Long notificationCount = redisTemplate.opsForList().size(clientNumber);
            if(notificationCount!=null && notificationCount>=10){

                return ResponseData.of(null, HttpStatus.UNPROCESSABLE_ENTITY);
            }

            redisTemplate.opsForList().leftPush(clientNumber,notificationData);

            return ResponseData.of(null);
        }
        catch (Exception ex){
            LOGGER.error(" ",ex);
            return ResponseData.of(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseData<List<NotificationData>> getNotifications(RequestData<Void> req){

        Map<String,Object> params = req.getPathParams();
        String clientNumber = params.get("client").toString();

        List<NotificationData> notifications = redisTemplate.opsForList().range(clientNumber,0,-1);
        return ResponseData.of(notifications);
    }

}
