package com.kyc.notifications.controllers;

import com.kyc.core.model.notifications.NotificationData;
import com.kyc.core.model.notifications.NotificationDetail;
import com.kyc.core.model.web.RequestData;
import com.kyc.notifications.delegate.NotificationDelegate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;

import static com.kyc.core.constants.GeneralConstants.ID_RECIPIENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationDelegate delegate;

    private NotificationData notificationData;
    private HttpHeaders httpHeaders;

    private JacksonTester<Object> jacksonTester;

    @BeforeEach
    public void setUp(){

        notificationData = new NotificationData();
        notificationData.setMessage("TEST");
        notificationData.setEvent("INFO");

        httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION,"token");
        httpHeaders.add("channel","channel");
        httpHeaders.add(ID_RECIPIENT,"999");

        JsonMapper mapper = new JsonMapper();
        JacksonTester.initFields(this,mapper);
    }

    @Test
    public void addNotification_processRequest_returnSuccessfulResponse() throws Exception{

        String body = jacksonTester.write(notificationData).getJson();

        given(delegate.addNotification(any(RequestData.class)))
                .willReturn(ResponseEntity.ok(null));

        mockMvc.perform(post("/notification")
                .headers(httpHeaders)
                .content(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void getNotifications_processRequest_returnSuccessfulResponse() throws Exception{

        given(delegate.getNotifications(any(RequestData.class)))
                .willReturn(ResponseEntity.ok(Collections.singleton(new NotificationDetail())));

        mockMvc.perform(get("/").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
