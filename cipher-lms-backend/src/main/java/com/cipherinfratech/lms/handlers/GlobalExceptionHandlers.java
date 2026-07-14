package com.cipherinfratech.lms.handlers;

import com.cipherinfratech.lms.utils.ResponseModels;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.NonUniqueResultException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlers {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseModels.customFail(HttpStatus.BAD_REQUEST, "Invalid request parameter");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException v) {
        return ResponseModels.customFail(HttpStatus.BAD_REQUEST, v.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return ResponseModels.customFail(HttpStatus.FORBIDDEN, "Access denied: You do not have permission to access this resource");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFountException(NotFoundException v) {
        return ResponseModels.customFail(HttpStatus.NOT_FOUND, v.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> exceptions(RuntimeException ex) {
        ex.printStackTrace();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", "Something went wrong");
        body.put("errors", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> exceptionsJwt(ExpiredJwtException ex) {
        ex.printStackTrace();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", "Something went wrong");
        body.put("errors", ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> httpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException");
        InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();    // Extract the problematic value
        Object invalidValue = invalidFormatException.getValue();    // Build a meaningful error message
        String errorMessage = String.format("Invalid value for %s', '%s' should be in '%s' type", invalidValue, invalidValue, invalidFormatException.getTargetType().getSimpleName());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", errorMessage);
        body.put("errors", ex.getMessage());
        System.out.println(body.get("errors"));
        System.out.println(body.get("errors"));
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        List<String> error = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", error.get(0));
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NonUniqueResultException.class)
    public ResponseEntity<Object> nonUniqueResult(NonUniqueResultException ex) {
        log.error("NonUniqueResultException");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", "Something went wrong, Data return more than one result");
        body.put("errors", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(value = IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<Object> nonUniqueResult(IncorrectResultSizeDataAccessException ex) {
        log.error("IncorrectResultSizeDataAccessException");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", "Something went wrong, Data return more than one result");
        body.put("errors", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> dataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        if (e.getMessage().contains("Duplicate entry")) {
            int start = e.getMessage().indexOf("Duplicate entry");
            int end = e.getMessage().indexOf("' for key");
            body.put("message", e.getMessage().substring(start + 17, end) + " already existed");
        } else if(e.getMessage().contains("Cannot add or update")){
            body.put("message","Data not found with selected field");
        }
        else body.put("message", "Cannot delete, Data assign to someone");
        body.put("errors", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(value = NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundException(NoHandlerFoundException ex) {
        log.error("NoHandlerFoundException ");
        log.error(String.valueOf(ex));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", ex.getLocalizedMessage());
        body.put("errors", ex.getLocalizedMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> globalException(Exception ex) {
        log.error("globalException ");
        log.error(String.valueOf(ex));
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", "Something went wrong");
        body.put("errors", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
