package com.rs.app.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
	@PostMapping("/upload/file")
	public ResponseEntity<Long> upload(@RequestParam(name = "position") Long position,
			@RequestParam(name = "file") MultipartFile multipartFile) {
		long filePointer = -1;
		String originalFileName = multipartFile.getOriginalFilename();
		File targetDir = new File("/tmp/uploads/");
		File parentDir = targetDir.getParentFile();
		System.out.println("ParentDir: " + parentDir);
		if (!targetDir.exists() || (targetDir.exists() && !targetDir.isDirectory())) {
			System.out.println("targetDir not exists..");
			targetDir.mkdirs();
		}
		int index = originalFileName.lastIndexOf('.');
		String fileName = originalFileName.substring(0, index);
		String filePath = targetDir + File.separator + fileName;
		File file = new File(filePath);
		try (InputStream inStream = multipartFile.getInputStream();
				RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
			randomAccessFile.seek(position);
			byte[] buff = new byte[1024 * 16];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buff)) != -1) {
				randomAccessFile.write(buff, 0, bytesRead);
			}
			filePointer = randomAccessFile.getFilePointer();

		} catch (IOException io) {
			System.err.println("Error in uploading file. " + io.getMessage());
			io.printStackTrace();
		}
		return ResponseEntity.ok(filePointer);
	}

	@PostMapping("upload/files")
	public String upload(@RequestParam(name = "file") MultipartFile[] multipartFiles) {
		Long position = 0L;
		for (MultipartFile multipartFile : multipartFiles) {
			position = uploadChunk(position, multipartFile);
		}
		return "Upload Successful";
	}

	public Long uploadChunk(Long filePointer, MultipartFile multipartFile) {
		long position = -1;
		String originalFileName = multipartFile.getOriginalFilename();
		File targetDir = new File("/tmp/uploads/");
		File parentDir = targetDir.getParentFile();
		System.out.println("ParentDir: " + parentDir);
		if (!targetDir.exists() || (targetDir.exists() && !targetDir.isDirectory())) {
			System.out.println("targetDir not exists..");
			targetDir.mkdirs();
		}
		int index = originalFileName.lastIndexOf('.');
		String fileName = originalFileName.substring(0, index);
		String filePath = targetDir + File.separator + fileName;
		File file = new File(filePath);
		try (InputStream inStream = multipartFile.getInputStream();
				RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
			randomAccessFile.seek(filePointer);
			byte[] buff = new byte[1024 * 16];
			int bytesRead = -1;
			while ((bytesRead = inStream.read(buff)) != -1) {
				randomAccessFile.write(buff, 0, bytesRead);
			}
			position = randomAccessFile.getFilePointer();

		} catch (IOException io) {
			System.err.println("Error in uploading file. " + io.getMessage());
			io.printStackTrace();
		}

		return position;
	}
}
