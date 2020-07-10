package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRServiceImpl is used for .....
 */
// TODO logger would be great
@Profile("default")
@Service
public class OCRServiceTPlatform implements OCRService {

  private final FileStorageService fileStorageService;
  private HashMap<String, TessBaseAPI> byLanguageTPlatform;

  public OCRServiceTPlatform(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
    this.byLanguageTPlatform = new HashMap<>();
    addTesseractLanguage("eng");
  }

  @Override
  public Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality) {
    if (!addTesseractLanguage(lang)) {
      // TODO return error - unsupported language!
      return null;
    }

//    String savedPath = fileStorageService.saveFile(file.to);
//
//    return extractTextFromFile(savedPath, lang, highQuality);
    return null;
  }

  @Override
  public Document extractTextFromFile(String savedPath, String lang, Boolean highQuality) {
    if (savedPath.length() <= 0) throw new RuntimeException("File not here");

    TessBaseAPI tessBaseAPI = byLanguageTPlatform.get("eng");

    PIX image = pixRead(savedPath);

    tessBaseAPI.SetImage(image);

    // Get OCR result
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    System.out.println("Text extracted of length:\n" + outText.getString().length());

    String extractedText = outText.getString();

    // Destroy used object and release memory
    outText.deallocate();
    pixDestroy(image);
    try {
      Files.deleteIfExists(new File(savedPath).toPath());
    } catch (IOException e) {
      System.err.println("Problem with deleting file: " + e.getMessage());
      e.printStackTrace();
    }

    return new Document(savedPath.substring(savedPath.lastIndexOf('/') + 1), "", extractedText);
  }

  @Override
  public boolean addTesseractLanguage(String language) {
    if (byLanguageTPlatform.containsKey(language)) return true;

    TessBaseAPI newTessBaseAPI = new TessBaseAPI();

    if (newTessBaseAPI.Init(PATH_TO_TESSDATA, language) != 0) {
      System.err.println("Could not initialize tesseract, with language: " + language);
      return false;
    }
    byLanguageTPlatform.put(language, newTessBaseAPI);
    return true;
  }
}
