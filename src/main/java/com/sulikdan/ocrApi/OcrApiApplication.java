package com.sulikdan.ocrApi;

import com.sulikdan.ocrApi.services.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
  @Resource FileStorageService fileStorageService;

  @Value("${tesseract.path}" )
  public static String pathToTessdata;
//  public static String pathToTessdata = "/usr/share/tessdata/";

  public static void main(String[] args) {
//    System.out.println("Input arguments:\n" + Arrays.toString(args) + "\n");
//    if (args.length >= 2 && args[0].contains("tessdata") && !args[1].isEmpty()) {
//      pathToTessdata = args[1];
//      log.info("Path to test data: " + pathToTessdata);
//    }

    log.info("Chosen path to tessdata: " + pathToTessdata);
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
