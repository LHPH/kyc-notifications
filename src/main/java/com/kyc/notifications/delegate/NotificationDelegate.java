package com.kyc.notifications.delegate;

import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.notifications.model.NotificationData;
import com.kyc.notifications.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationDelegate {

    @Autowired
    private NotificationService notificationService;


    public ResponseEntity<ResponseData<Void>> addNotification(RequestData<NotificationData> req){

        return notificationService.addNotification(req).toResponseEntity();
    }

    public ResponseEntity<ResponseData<List<NotificationData>>> getNotifications(RequestData<Void> req){

        return notificationService.getNotifications(req).toResponseEntity();
    }

}
