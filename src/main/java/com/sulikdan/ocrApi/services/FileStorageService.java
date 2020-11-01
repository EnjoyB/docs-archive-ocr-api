package com.sulikdan.ocrApi.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Created by Daniel Šulik on 02-Jul-20
 *
 * <p>Class FileStorageService is used for work with received files - saving, reading, deleting.
 */
public interface FileStorageService {

  public void init();

  public Path saveFile(MultipartFile file, String filePrefixName);

  public Path saveTmpFile(BufferedImage bufferedImage, int pageNum, String filePrefixName);

  public void deleteFile(Path file);

  public Resource loadFile(String filename);

  public void deleteAllFiles();

  public Stream<Path> loadAllFiles();
}
