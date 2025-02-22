package com.kyc.notifications.consumers;

import com.kyc.core.model.notifications.NotificationData;
import com.kyc.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationCustomerConsumerTest {

    @InjectMocks
    private NotificationCustomerConsumer consumer;

    @Mock
    private NotificationService service;

    @Test
    public void receiverMessage_receiveData_dataWasProcessed(){

        consumer.receiverMessage(new NotificationData(),"token","9999","channel");
        verify(service,times(1)).addNotification(any(NotificationData.class),anyString(),anyString(),anyString());
    }
}
