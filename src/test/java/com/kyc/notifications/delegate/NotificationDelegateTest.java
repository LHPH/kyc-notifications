package com.kyc.notifications.delegate;

import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.notifications.model.NotificationData;
import com.kyc.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationDelegateTest {

    @InjectMocks
    private NotificationDelegate delegate;

    @Mock
    private NotificationService service;

    @BeforeAll
    public static void setUp(){
        MockitoAnnotations.openMocks(NotificationDelegateTest.class);
    }

    @Test
    public void addNotification_processRequest_returnSuccessfulResponse(){

        given(service.addNotification(any(RequestData.class)))
                .willReturn(ResponseData.of(null));

        delegate.addNotification(RequestData.<NotificationData>builder().build());
        verify(service,times(1)).addNotification(any(RequestData.class));
    }

    @Test
    public void getNotifications_processRequest_returnSuccessfulResponse(){

        given(service.getNotifications(any(RequestData.class)))
                .willReturn(ResponseData.of(new ArrayList<>()));

        delegate.getNotifications(RequestData.<Void>builder().build());
        verify(service,times(1)).getNotifications(any(RequestData.class));
    }
}
