package com.sulikdan.ocrApi;

import com.sulikdan.ocrApi.services.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
@EnableAsync
public class OcrApiApplication implements CommandLineRunner {

  @Resource
  FileStorageService fileStorageService;

//  public static String pathToTessdata = "empty";

  public static void main(String[] args) {
    SpringApplication.run(OcrApiApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    fileStorageService.deleteAllFiles();
    fileStorageService.init();
  }

  @Bean("threadPoolTaskExecutor")
  public TaskExecutor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("AsynchRest-");
    executor.initialize();
    return executor;
  }
}
