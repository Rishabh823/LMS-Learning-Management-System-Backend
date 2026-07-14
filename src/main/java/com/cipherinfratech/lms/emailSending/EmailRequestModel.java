package com.cipherinfratech.lms.emailSending;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailRequestModel {
    private String to;
    private String subject;
    private String body;
    private String payload;
}
