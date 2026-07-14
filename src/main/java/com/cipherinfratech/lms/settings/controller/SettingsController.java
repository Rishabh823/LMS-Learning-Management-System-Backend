package com.cipherinfratech.lms.settings.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cipherinfratech.lms.settings.models.CourseLanguage;
import com.cipherinfratech.lms.settings.models.CourseLavel;
import com.cipherinfratech.lms.settings.models.CourseType;

@RestController
@RequestMapping("/settings")
@CrossOrigin
public class SettingsController {

	@GetMapping("/courseTypes")
	public ResponseEntity<Object> getAllCourseTypes() {
		CourseType[] allCourseType = CourseType.values();
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("messages", "All Course Types");
		data.put("data", allCourseType);
		data.put("count", allCourseType.length);
		
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/courseLanguage")
	public ResponseEntity<Object> getAllCourseLanguage() {
		CourseLanguage[] allCourseLanguage = CourseLanguage.values();
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("messages", "All Course Languages");
		data.put("data", allCourseLanguage);
		data.put("count", allCourseLanguage.length);
		return ResponseEntity.ok(data);
	}
	
	@GetMapping("/courseLavel")
	public ResponseEntity<Object> getAllCourseLavel() {
		CourseLavel[] allCourseLavel = CourseLavel.values();
		Map<String, Object> data = new HashMap<>();
		data.put("status", "success");
		data.put("data", allCourseLavel);
		data.put("messages", "All Course Lavels");
		data.put("count", allCourseLavel.length);
		return ResponseEntity.ok(data);
	}
	
}
