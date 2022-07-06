package com.kyc.notifications.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class NotificationData {

    @NotNull
    @Pattern(regexp = "^[A-Z\\s\\d]{50}",message = "Invalid format")
    private String message;

    @NotNull
    @Pattern(regexp = "^[A-Z\\s\\d]{15}",message = "Invalid format")
    private String event;

    private Date date;
}
