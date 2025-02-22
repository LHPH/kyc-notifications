package com.kyc.notifications.controllers;

import com.kyc.core.model.notifications.NotificationData;
import com.kyc.core.model.notifications.NotificationDetail;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.notifications.delegate.NotificationDelegate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kyc.core.constants.GeneralConstants.CHANNEL;
import static com.kyc.core.constants.GeneralConstants.ID_RECIPIENT;

@RestController
@Validated
public class NotificationController {

    @Autowired
    private NotificationDelegate delegate;

    @PostMapping("/notification")
    public ResponseEntity<ResponseData<Void>> addNotification(@RequestHeader(ID_RECIPIENT) Long userIdRecipient,
                                                              @RequestBody @Valid NotificationData notificationData){

        Map<String,Object> headers = new HashMap<>();
        headers.put(ID_RECIPIENT,userIdRecipient);

        RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                .headers(headers)
                .body(notificationData)
                .build();

        return delegate.addNotification(req);
    }

    @GetMapping("/")
    public ResponseEntity<ResponseData<List<NotificationDetail>>> getNotifications(@RequestHeader(CHANNEL) String channel){

        Map<String,Object> params = new HashMap<>();
        params.put(CHANNEL,channel);

        RequestData<Void> req = RequestData.<Void>builder()
                .headers(params)
                .build();

        return delegate.getNotifications(req);
    }
}
