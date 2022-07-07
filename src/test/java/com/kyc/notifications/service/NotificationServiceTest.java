package com.kyc.notifications.service;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.notifications.model.NotificationData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService service;

    @Mock
    private RedisTemplate<String, NotificationData> redisTemplate;

    @Mock
    private KycMessages kycMessages;

    private ListOperations<String,NotificationData> listOperations;

    @BeforeAll
    public static void init(){
        MockitoAnnotations.openMocks(NotificationServiceTest.class);
    }

    @BeforeEach
    public void setUp(){

        ReflectionTestUtils.setField(service,"numberNotificationsByUser",10);

        listOperations = mock(ListOperations.class);
        given(redisTemplate.opsForList()).willReturn(listOperations);

    }

    @Test
    public void addNotification_savingNotification_notificationWasSaved(){

        Map<String,Object> params = new HashMap<>();
        params.put("sender","1");
        params.put("receiver","2");

        RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                .pathParams(params)
                .body(new NotificationData())
                .build();


        given(listOperations.size(anyString())).willReturn(8L);

        ResponseData<Void> response = service.addNotification(req);
        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
    }

    @Test
    public void addNotification_overpassLimitNotifications_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            Map<String,Object> params = new HashMap<>();
            params.put("sender","1");
            params.put("receiver","2");

            RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                    .pathParams(params)
                    .body(new NotificationData())
                    .build();

            given(listOperations.size(anyString())).willReturn(10L);
            service.addNotification(req);
        });
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,ex.getStatus());
    }

    @Test
    public void addNotification_unavailableService_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            Map<String,Object> params = new HashMap<>();
            params.put("sender","1");
            params.put("receiver","2");

            RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                    .pathParams(params)
                    .body(new NotificationData())
                    .build();

            given(listOperations.size(anyString())).willReturn(5L);
            given(listOperations.leftPush(anyString(),any(NotificationData.class)))
                    .willThrow(new InvalidDataAccessResourceUsageException("test error"));
            service.addNotification(req);
        });
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE,ex.getStatus());
    }

    @Test
    public void getNotifications_retrieveNotifications_returnNotifications(){

        Map<String,Object> params = new HashMap<>();
        params.put("client","1");

        RequestData<Void> req = RequestData.<Void>builder()
                .pathParams(params)
                .build();

        given(listOperations.size(anyString())).willReturn(10L);
        given(listOperations.leftPop(anyString(),anyLong()))
                .willReturn(Collections.singletonList(new NotificationData()));

        ResponseData<List<NotificationData>> response = service.getNotifications(req);

        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
        Assertions.assertFalse(response.getData().isEmpty());
    }

    @Test
    public void getNotifications_noNotifications_returnZeroNotifications(){

        Map<String,Object> params = new HashMap<>();
        params.put("client","1");

        RequestData<Void> req = RequestData.<Void>builder()
                .pathParams(params)
                .build();

        given(listOperations.size(anyString())).willReturn(0L);

        ResponseData<List<NotificationData>> response = service.getNotifications(req);

        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
        Assertions.assertTrue(response.getData().isEmpty());
    }

    @Test
    public void getNotifications_unavailableService_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            Map<String,Object> params = new HashMap<>();
            params.put("client","1");

            RequestData<Void> req = RequestData.<Void>builder()
                    .pathParams(params)
                    .build();

            given(listOperations.size(anyString())).willReturn(10L);
            given(listOperations.leftPop(anyString(),anyLong()))
                    .willThrow(new InvalidDataAccessResourceUsageException("test error"));

            service.getNotifications(req);
        });
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE,ex.getStatus());
    }

    @Test
    public void getNotificationsCountByCustomer_consultNumber_returnNumberNotifications(){

        given(listOperations.size(anyString())).willReturn(10L);

        Long num = service.getNotificationsCountByCustomer("1");
        Assertions.assertEquals(10L,num);
    }

    @Test
    public void getNotificationsCountByCustomer_returnNullResponse_returnZeroNotifications(){

        given(listOperations.size(anyString())).willReturn(null);

        Long num = service.getNotificationsCountByCustomer("1");
        Assertions.assertEquals(0L,num);
    }

    @Test
    public void getNotificationsCountByCustomer_unavailableService_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            given(listOperations.size(anyString()))
                    .willThrow(new InvalidDataAccessResourceUsageException("test error"));
            service.getNotificationsCountByCustomer("1");
        });
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE,ex.getStatus());
    }
}
