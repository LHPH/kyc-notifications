package com.kyc.notifications.controllers;

import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.notifications.delegate.NotificationDelegate;
import com.kyc.notifications.model.NotificationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationDelegate delegate;

    @PostMapping("/notification")
    public ResponseEntity<ResponseData<Void>> addNotification(@RequestHeader("Authorization") String token,
                                                              @RequestHeader("kyc-customer-id-receiver") Long customerId,
                                                              @RequestHeader("channel") String channel,
                                                              @RequestBody NotificationData notificationData){

        Map<String,Object> params = new HashMap<>();
        params.put("sender",token);
        params.put("receiver",customerId);
        params.put("channel",channel);

        RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                .pathParams(params)
                .body(notificationData)
                .build();

        return delegate.addNotification(req);
    }

    @GetMapping("/notifications")
    public ResponseEntity<ResponseData<List<NotificationData>>> getNotifications(@RequestHeader("Authorization") String token,
                                                                                 @RequestHeader("channel") String channel){

        Map<String,Object> params = new HashMap<>();
        params.put("client",9999);
        params.put("channel",channel);

        RequestData<Void> req = RequestData.<Void>builder()
                .pathParams(params)
                .build();

        return delegate.getNotifications(req);
    }
}
