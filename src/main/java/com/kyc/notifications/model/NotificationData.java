package com.kyc.notifications.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class NotificationData {

    private String message;
    private String event;
    private Date date;
}
