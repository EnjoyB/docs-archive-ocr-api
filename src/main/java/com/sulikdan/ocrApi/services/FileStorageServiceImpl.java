package com.sulikdan.ocrApi.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Daniel Šulik on 02-Jul-20
 *
 * <p>Class FileStorageServiceImpl is used for .....
 */
@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

  private static final String STORAGE_FOLDER_NAME = "fileStorage";
  private static final Path BASE_PATH = Paths.get(STORAGE_FOLDER_NAME);

  @Override
  public void init() {
    try {
      Files.createDirectory(BASE_PATH);
    } catch (IOException e) {
      throw new RuntimeException(
          MessageFormat.format(
              "Wasn't able to initialize folder-{} for file uploading!", BASE_PATH));
    }
  }

  @Override
  public String saveFile(MultipartFile file) {
    try {
      Files.copy(
          file.getInputStream(),
          FileStorageServiceImpl.BASE_PATH.resolve(
              Objects.requireNonNull(file.getOriginalFilename())));
      return BASE_PATH + "/" + file.getOriginalFilename();
    } catch (Exception e) {
      throw new RuntimeException(
          MessageFormat.format(
              "Wasn't able to store the file.\nReceived error: {}", e.getMessage()));
    }
  }

  @Override
  public Resource loadFile(String filename) {
    try {
      Path file = BASE_PATH.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException(
            MessageFormat.format("Wasn't able to read the file with name {}!", filename));
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  @Override
  public void deleteAllFiles() {
    FileSystemUtils.deleteRecursively(BASE_PATH.toFile());
  }

  @Override
  public Stream<Path> loadAllFiles() {
    try {
      return Files.walk(FileStorageServiceImpl.BASE_PATH, 1)
          .filter(path -> !path.equals(FileStorageServiceImpl.BASE_PATH))
          .map(FileStorageServiceImpl.BASE_PATH::relativize);
    } catch (IOException e) {
      throw new RuntimeException("Could not load all files!");
    }
  }
}
