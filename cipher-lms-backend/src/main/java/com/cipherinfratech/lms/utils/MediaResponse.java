package com.cipherinfratech.lms.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaResponse {
    private byte[] data;
    private String contentType;
}
