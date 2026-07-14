package com.cipherinfratech.lms.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cipherinfratech.lms.users.services.UserService;

@Component
public class FileUploadHelper {
	@Autowired
	private UserService userService;

	public final String path = new ClassPathResource("/static").getFile().getAbsolutePath();
	//public final String pathProfileImages = new ClassPathResource("/static/images/profile").getFile().getAbsolutePath();
	//public final String chapterNotes = new ClassPathResource("/static/chapter_notes").getFile().getAbsolutePath();

	public FileUploadHelper() throws IOException {
		super();
	}

	/**
	 * Upload File/Image
	 * 
	 * @author Mayank Jyoti Verma
	 * @param MultipartFile file
	 * @return fileName
	 */
	public String uploadProfilePic(MultipartFile file, UUID userId) {

		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = timestamp.getTime() + "_" + file.getOriginalFilename();
			String pathProfileImages = path + "/images_profile";
			//Directory create if not existed
			File  f = new File(pathProfileImages);
			if(!f.exists()) {
				f.mkdir();
			}
			
			Files.copy(file.getInputStream(), Paths.get(pathProfileImages + File.separator + fileName),
					StandardCopyOption.REPLACE_EXISTING);
			// Files.copy(file.getInputStream(), path);
			boolean updateProfile = userService.updateProfilePic(userId, fileName);
			if (updateProfile) {
				return fileName;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	/**
	 * Upload chapter Notes and Screenshoots
	 * 
	 * @author Mayank Jyoti Verma
	 * @param MultipartFile file
	 * @return fileName
	 */
	public String uploadChapterNotes(MultipartFile file) {

		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = timestamp.getTime() + "_chapter_" + file.getOriginalFilename();
			String chapterNotesPath = path + "/chapter_notes";
			//Directory create if not existed
			File  f = new File(chapterNotesPath);
			if(!f.exists()) {
				f.mkdir();
			}
//			
			Files.copy(file.getInputStream(), Paths.get(chapterNotesPath + File.separator + fileName),
					StandardCopyOption.REPLACE_EXISTING);
			// Files.copy(file.getInputStream(), path);
			//boolean updateProfile = userService.updateProfilePic(userId, fileName);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}


	public String uploadFile(MultipartFile file) {
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String fileName = timestamp.getTime() + "_" + file.getOriginalFilename();
			String filePath = path + "/file_notes";

			// Create directory if it doesn't exist
			File directory = new File(filePath);
			if (!directory.exists()) {
				directory.mkdir();
			}

			Files.copy(file.getInputStream(), Paths.get(filePath + File.separator + fileName),
					StandardCopyOption.REPLACE_EXISTING);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
