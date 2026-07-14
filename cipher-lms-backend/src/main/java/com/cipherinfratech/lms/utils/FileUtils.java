package com.cipherinfratech.lms.utils;

import com.cipherinfratech.lms.handlers.ValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class FileUtils {

    public static byte[] compressFile(byte[] data) {

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];

        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        try {
            outputStream.close();
        } catch (Exception ignored) {

        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressFile(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4 * 1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
            outputStream.close();
        } catch (Exception ignored) {
        }
        return outputStream.toByteArray();

    }

    /**
     * Derives content type from the filename extension rather than trusting
     * the client-declared Content-Type, which some clients (e.g. Postman,
     * when a file is swapped in a form-data row) send stale/incorrect.
     */
    public static String resolveContentType(String filename, String declaredContentType) {

        if (filename != null) {

            String lower = filename.toLowerCase();

            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".pdf")) return "application/pdf";
            if (lower.endsWith(".xls")) return "application/vnd.ms-excel";
            if (lower.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        return declaredContentType;
    }

    public static void validateFile(
            MultipartFile file,
            List<String> allowedContentTypes,
            long maxSizeInMB
    ) {

        // null / empty check
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        // type check
        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.contains(contentType)) {
            throw new ValidationException(
                    "Invalid file type. Allowed types: " + allowedContentTypes
            );
        }

        // size check
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        if (file.getSize() > maxSizeInBytes) {
            throw new ValidationException(
                    "File size should not exceed " + maxSizeInMB + " MB"
            );
        }
    }



}
