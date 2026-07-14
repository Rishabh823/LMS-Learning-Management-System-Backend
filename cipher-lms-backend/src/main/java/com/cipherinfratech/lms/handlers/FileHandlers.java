package com.cipherinfratech.lms.handlers;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.cipherinfratech.lms.utils.ResponseModels;

@ControllerAdvice
public class FileHandlers {
	
	@ExceptionHandler(SizeLimitExceededException.class)
	public ResponseEntity<Object> handlerSizeLimitExceeded(SizeLimitExceededException ex) {
		
		return ResponseModels.error(
				"File size is "+ex.getActualSize()/1000000+"MB which is greater than "+ 10485760/1000000 +"MB");
	}

}
