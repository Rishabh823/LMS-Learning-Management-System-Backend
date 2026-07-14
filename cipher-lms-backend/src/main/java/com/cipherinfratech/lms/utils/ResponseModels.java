package com.cipherinfratech.lms.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseModels {

//	helper method to convert xml to json response
	private static HttpHeaders createJsonHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	/**
	 * success
	 * 
	 * @author mayankjyotiverma
	 * @param message
	 * @return success message , 200 status code
	 */
	public static ResponseEntity<Object> success(String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}
	public static ResponseEntity<Object> status(String key,boolean status, String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put(key, status);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	/**
	 * successWithPayload
	 * 
	 * @author mayankjyotiverma
	 * @param message
	 * @param payload
	 * @return success message ,payload, 200 status code
	 */
	public static ResponseEntity<Object> successWithPayload(String message, Vector<Object> payload) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", payload);
		data.put("count", payload.size());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}
	public static ResponseEntity<Object> successWithPayload(String message, List<?> payload) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", payload);
		data.put("count", payload.size());
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}
	public static ResponseEntity<Object> successWithPayload(String message, Object payload) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", payload);
		data.put("count", 1);
		return new ResponseEntity<Object>(data, HttpStatus.OK );
	}

	public static ResponseEntity<Object> successWithPayloadPaginated(String message, Page<?> page) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", page.getContent());
		data.put("totalItems", page.getTotalElements());
		data.put("totalPages", page.getTotalPages());
		data.put("currentPage", page.getNumber());
		return new ResponseEntity<>(data, createJsonHeaders(), HttpStatus.OK);
	}

	/**
	 * create
	 * 
	 * @author mayankjyotiverma
	 * @param message
	 * @return created new data, 201 status code
	 */
	public static ResponseEntity<Object> create(String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		return new ResponseEntity<Object>(data, HttpStatus.CREATED);
	}

	/**
	 * createWithPayload
	 * 
	 * @author mayankjyotiverma
	 * @param message
	 * @param payload
	 * @return created new data and return created data, 201 status code
	 */
	public static ResponseEntity<Object> createWithPayload(String message, Vector<Object> payload) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", payload);
		data.put("count", payload.size());
		return new ResponseEntity<>(data, HttpStatus.CREATED);
	}
	public static ResponseEntity<Object> createWithPayload(String message, Object payload) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		data.put("data", payload);
		data.put("count", 1);
		return new ResponseEntity<>(data, HttpStatus.CREATED);
	}

	/**
	 * Update
	 * 
	 * @author mayankjyotiverma
	 * @param message
	 * @return message, 200 status code
	 */
	public static ResponseEntity<Object> update(String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}
/**
	 * Deleted
	 *
	 * @author mayankjyotiverma
	 * @param message
	 * @return message, 200 status code
	 */
	public static ResponseEntity<Object> deleted(String message) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", message);
		return new ResponseEntity<Object>(data, HttpStatus.ACCEPTED);
	}

	/**
	 * Custom validation error
	 * 
	 * @param fieldName    : field name which is not valid
	 * @param errorMessage : error message for validation
	 * @param status       : 200 : Ok, Request success but fail in validation
	 * @return Return custom error Response Data
	 */
	public static ResponseEntity<Object> customValidations(String fieldName, String errorMessage) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("fieldName", fieldName);
		data.put("message", errorMessage);
		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	/**
	 * Response error
	 * 
	 * @author Mayank Jyoti Verma
	 * @param status 400 - BAD REQUEST
	 * @return ResponseEntity<Object>(error message, staus);
	 */
	public static ResponseEntity<Object> unknownError() {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("message", "Something went wrong");
		return new ResponseEntity<Object>(data, HttpStatus.BAD_REQUEST);
	}
	public static ResponseEntity<Object> exceptionError(Exception e) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("message", "Something went wrong");
		data.put("error", e.getMessage());
		return new ResponseEntity<Object>(data, HttpStatus.BAD_REQUEST);
	}

	public static ResponseEntity<Object> sessionExpire() {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("message", "Session expire");
		return new ResponseEntity<Object>(data, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Response error
	 * 
	 * @author Mayank Jyoti Verma
	 * @param status 400 - BAD REQUEST
	 * @return ResponseEntity<Object>(error message, staus);
	 */
	public static ResponseEntity<Object> error(String errorMessage) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("message", errorMessage);
		return new ResponseEntity<Object>(data, HttpStatus.BAD_REQUEST);
	}

	/**
	 * Response error
	 * 
	 * @author Mayank Jyoti Verma
	 * @param status 400 - BAD REQUEST
	 * @return ResponseEntity<Object>(error message, staus);
	 */
	public static ResponseEntity<Object> unsupportedMediaType(String errorMessage) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "fail");
		data.put("message", errorMessage);
		return new ResponseEntity<Object>(data, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}

	public static ResponseEntity<Object> fileUploaded(String fileName, String fileType) {
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("message", "File uploaded successfully");
		data.put("fileName", fileName);
		data.put("fileType", fileType);

		return new ResponseEntity<Object>(data, HttpStatus.OK);
	}

	public static ResponseEntity<Object> requestedAccepted() {
		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}
	public static ResponseEntity<Object> NoContentAccepted() {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

	/**
	 * Send Media file in Original format. Decompress the file data and send
	 * @param fileData compressed data
	 * @param fileType string
	 * @return Original media file
	 */
	public static ResponseEntity<Object> sendMedia(byte[] fileData, String fileType) {
		return ResponseEntity.status(HttpStatus.OK).cacheControl(CacheControl.noStore()).contentType(MediaType.valueOf(fileType)).body(FileUtils.decompressFile(fileData));
	}

	/**
	 * Send Media in Original format
	 * @author mayankjyotiverma
	 * @param fileData - original file data in byte
	 * @param fileType- type of the file like jpeg, jpg, pdf etc
	 * @return file as original format
	 */
	public static ResponseEntity<Object> sendMediaWithDecompress(byte[] fileData, String fileType) {
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf(fileType)).body(FileUtils.decompressFile(fileData));
	}

	public static ResponseEntity<Object> customFail(HttpStatus httpStatus,String message){
		return buildResponse(httpStatus,"fail",message,null);
	}

	private static ResponseEntity<Object> buildResponse(HttpStatus httpStatus, String status, String message, Map<String, Object> extra) {
		Map<String, Object> data = new HashMap<>();
		if (status != null) data.put("status", status);
		if (message != null) data.put("message", message);
		if (extra != null) data.putAll(extra);
		return new ResponseEntity<>(data, httpStatus);
	}


}

//1×× Informational
//100 Continue
//101 Switching Protocols
//102 Processing
//103 Early Hints

//2×× Success
//200 OK
//201 Created
//202 Accepted
//203 Non-authoritative Information
//204 No Content
//205 Reset Content
//206 Partial Content
//207 Multi-Status
//208 Already Reported
//226 IM Used

//3×× Redirection
//300 Multiple Choices
//301 Moved Permanently
//302 Found
//303 See Other
//304 Not Modified
//305 Use Proxy
//307 Temporary Redirect
//308 Permanent Redirect

//4×× Client Error
//400 Bad Request
//401 Unauthorized
//402 Payment Required
//403 Forbidden
//404 Not Found
//405 Method Not Allowed
//406 Not Acceptable
//407 Proxy Authentication Required
//408 Request Timeout
//409 Conflict
//410 Gone
//411 Length Required
//412 Precondition Failed
//413 Payload Too Large
//414 Request-URI Too Long
//415 Unsupported Media Type
//416 Requested Range Not Satisfiable
//417 Expectation Failed
//418 I'm a teapot
//421 Misdirected Request
//422 Unprocessable Entity
//423 Locked
//424 Failed Dependency
//425 Too Early
//426 Upgrade Required
//428 Precondition Required
//429 Too Many Requests
//431 Request Header Fields Too Large
//444 Connection Closed Without Response
//451 Unavailable For Legal Reasons
//499 Client Closed Request

//5×× Server Error
//500 Internal Server Error
//501 Not Implemented
//502 Bad Gateway
//503 Service Unavailable
//504 Gateway Timeout
//505 HTTP Version Not Supported
//506 Variant Also Negotiates
//507 Insufficient Storage
//508 Loop Detected
//510 Not Extended
//511 Network Authentication Required
//599 Network Connect Timeout Error
