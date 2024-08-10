package com.kyc.notifications.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull
    @Pattern(regexp = "^[A-Z\\s\\d]{1,50}",message = "Invalid format")
    private String message;

    @NotNull
    @Pattern(regexp = "^[A-Z\\s\\d]{1,15}",message = "Invalid format")
    private String event;

    private Date date;
}
