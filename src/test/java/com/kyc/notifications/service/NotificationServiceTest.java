package com.kyc.notifications.service;

import com.kyc.core.exception.KycRestException;
import com.kyc.core.model.jwt.JwtData;
import com.kyc.core.model.notifications.NotificationData;
import com.kyc.core.model.notifications.NotificationDetail;
import com.kyc.core.model.web.RequestData;
import com.kyc.core.model.web.ResponseData;
import com.kyc.core.properties.KycMessages;
import com.kyc.core.util.TokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kyc.core.constants.GeneralConstants.ID_RECIPIENT;
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
    private RedisTemplate<String, NotificationDetail> redisTemplate;

    @Mock
    private KycMessages kycMessages;

    private ListOperations<String,NotificationDetail> listOperations;

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
        params.put(ID_RECIPIENT,"2");

        RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                .headers(params)
                .body(new NotificationData())
                .build();

        loadMethodSecurity();
        given(listOperations.size(anyString())).willReturn(8L);

        ResponseData<Void> response = service.addNotification(req);
        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
    }

    @Test
    public void addNotification_overpassLimitNotifications_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            Map<String,Object> params = new HashMap<>();
            params.put(ID_RECIPIENT,"2");

            RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                    .headers(params)
                    .body(new NotificationData())
                    .build();

            loadMethodSecurity();
            given(listOperations.size(anyString())).willReturn(10L);
            service.addNotification(req);
        });
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,ex.getStatus());
    }

    @Test
    public void addNotification_unavailableService_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            Map<String,Object> params = new HashMap<>();
            params.put(ID_RECIPIENT,"2");

            RequestData<NotificationData> req = RequestData.<NotificationData>builder()
                    .headers(params)
                    .body(new NotificationData())
                    .build();

            loadMethodSecurity();
            given(listOperations.size(anyString())).willReturn(5L);
            given(listOperations.leftPush(anyString(),any(NotificationDetail.class)))
                    .willThrow(new InvalidDataAccessResourceUsageException("test error"));
            service.addNotification(req);
        });
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE,ex.getStatus());
    }

    @Test
    public void getNotifications_retrieveNotifications_returnNotifications(){

        RequestData<Void> req = RequestData.<Void>builder()
                .build();

        loadMethodSecurity();
        given(listOperations.size(anyString())).willReturn(10L);
        given(listOperations.leftPop(anyString(),anyLong()))
                .willReturn(Collections.singletonList(new NotificationDetail()));

        ResponseData<List<NotificationDetail>> response = service.getNotifications(req);

        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
        Assertions.assertFalse(response.getData().isEmpty());
    }

    @Test
    public void getNotifications_noNotifications_returnZeroNotifications(){

        RequestData<Void> req = RequestData.<Void>builder()
                .build();

        loadMethodSecurity();
        given(listOperations.size(anyString())).willReturn(0L);

        ResponseData<List<NotificationDetail>> response = service.getNotifications(req);

        Assertions.assertEquals(HttpStatus.OK,response.getHttpStatus());
        Assertions.assertTrue(response.getData().isEmpty());
    }

    @Test
    public void getNotifications_unavailableService_throwError(){

        KycRestException ex = Assertions.assertThrows(KycRestException.class,()->{

            RequestData<Void> req = RequestData.<Void>builder()
                    .build();

            loadMethodSecurity();
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

    private void loadMethodSecurity(){

        JwtAuthenticationToken jwtAuthenticationToken = Mockito.mock(JwtAuthenticationToken.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        given(securityContext.getAuthentication()).willReturn(jwtAuthenticationToken);
        given(jwtAuthenticationToken.getToken()).willReturn(getJwt());

        SecurityContextHolder.setContext(securityContext);
    }

    private Jwt getJwt(){

        JwtData jwtData = JwtData.builder()
                .channel("ONLINE")
                .owner(1L)
                .role("CUSTOMER")
                .exp(System.currentTimeMillis()+50000)
                .iat(System.currentTimeMillis())
                .iss("http://localhost:8080")
                .sub("SUB")
                .addAud("http://localhost:9000")
                .header("alg","HMAC256")
                .addition("claim","value")
                .user(1L)
                .build();

        return TokenUtil.transform("token",jwtData);
    }
}
