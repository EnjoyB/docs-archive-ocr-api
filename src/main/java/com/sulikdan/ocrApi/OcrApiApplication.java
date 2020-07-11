package com.sulikdan.ocrApi;

import com.sulikdan.ocrApi.services.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
public class OcrApiApplication implements CommandLineRunner {
  @Resource FileStorageService fileStorageService;

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
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("AsynchRest-");
    executor.initialize();
    return executor;
  }
}
