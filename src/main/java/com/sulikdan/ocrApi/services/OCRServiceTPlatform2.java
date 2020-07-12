package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;

/**
 * Created by Daniel Šulik on 07-Jul-20
 *
 * <p>Class OCRServiceTPlatform2 is used for .....
 *
 * !!!! This is only dummy class, in case I want to create every-time new instance of tesseract API !!!!
 */
public class OCRServiceTPlatform2 implements OCRService {

  private final FileStorageService fileStorageService;

  public OCRServiceTPlatform2(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @Override
  public Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality) {

    TessBaseAPI tessBaseAPI = new TessBaseAPI();

    if (tessBaseAPI.Init(PATH_TO_TESSDATA, lang) != 0) {
      System.err.println("Could not initialize tesseract, with language: " + lang);
      throw new RuntimeException("Language not supported or could not initialize!");
    }

    Path savedPath = fileStorageService.saveFile(file, "xxxxx_TODO_xxxx");

    if (savedPath.toString().length() <= 0) throw new RuntimeException("File not here");

    PIX image = pixRead(savedPath.toString());

    tessBaseAPI.SetImage(image);

    // Get OCR result
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    System.out.println("Text extracted of length:\n" + outText.getString().length());

    String extractedText = outText.getString();

    // Destroy used object and release memory
    outText.deallocate();
    pixDestroy(image);
    tessBaseAPI.deallocate();
    try {
      Files.deleteIfExists(savedPath);
    } catch (IOException e) {
      System.err.println("Problem with deleting file: " + e.getMessage());
      e.printStackTrace();
    }

    return new Document(savedPath.getFileName().toString(), "", new ArrayList<String>(Collections.singleton(extractedText)));
  }

  @Override
  public Document extractTextFromFile(
          Path savedFilePath, String newFileName, String lang, Boolean multipageTiff, Boolean highQuality) {
    return null;
  }

  @Override
  public boolean addTesseractLanguage(String language) {
    throw new RuntimeException("Not needed!");
  }
}
