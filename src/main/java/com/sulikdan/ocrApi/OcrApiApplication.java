package com.sulikdan.ocrApi;

import com.sulikdan.ocrApi.services.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class OcrApiApplication implements CommandLineRunner {
	@Resource
	FileStorageService fileStorageService;

	public static void main(String[] args) {
		SpringApplication.run(OcrApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileStorageService.deleteAllFiles();
		fileStorageService.init();
	}

}
