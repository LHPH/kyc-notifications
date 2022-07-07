package com.kyc.notifications.consumers;

import com.kyc.core.model.web.RequestData;
import com.kyc.notifications.model.NotificationData;
import com.kyc.notifications.service.NotificationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationCustomerConsumerTest {

    @InjectMocks
    private NotificationCustomerConsumer consumer;

    @Mock
    private NotificationService service;

    @BeforeAll
    public static void init(){
        MockitoAnnotations.openMocks(NotificationCustomerConsumer.class);
    }

    @Test
    public void receiverMessage_receiveData_dataWasProcessed(){

        consumer.receiverMessage(new NotificationData(),"token","9999","channel");
        verify(service,times(1)).addNotification(any(RequestData.class));
    }
}
