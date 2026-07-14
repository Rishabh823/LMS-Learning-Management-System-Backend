package com.cipherinfratech.lms.utils;

import java.util.ArrayList;
import java.util.List;

public class FileFormats {

    public static List<String> userProfilePictureFormat() {
        List<String> imagesType = new ArrayList<>();
        imagesType.add("image/jpeg");
        imagesType.add("image/jpg");
        imagesType.add("image/png");
        return imagesType;
    }
public static List<String> liveTrainingFilesOrNotesFormat() {
        List<String> imagesType = new ArrayList<>();
        imagesType.add("image/jpeg");
        imagesType.add("image/jpg");
        imagesType.add("image/png");
        imagesType.add("applications/pdf");
        return imagesType;
    }

    public static List<String> documentUploadFormat() {
        List<String> allowedTypes = new ArrayList<>();
        allowedTypes.add("image/jpeg");
        allowedTypes.add("image/jpg");
        allowedTypes.add("image/png");
        allowedTypes.add("application/pdf");
        allowedTypes.add("application/vnd.ms-excel");
        allowedTypes.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return allowedTypes;
    }

}