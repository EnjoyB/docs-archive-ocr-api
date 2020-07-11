package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.PIXA;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRServiceImpl is used for .....
 */
@Slf4j
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
  public Document extractTextFromFile(
          MultipartFile file, String newFileName, String lang, Boolean multipageTiff, Boolean highQuality) {
    Document extractedFile = null;

    if ( multipageTiff ){

    } else {

    }

    return extractedFile;
  }

  //  @Override
  public Document extractTextFromFile(Path savedPath, String lang, Boolean highQuality) {
    if (savedPath.toString().length() <= 0) throw new RuntimeException("File not here");

    TessBaseAPI tessBaseAPI = byLanguageTPlatform.get("eng");

    ArrayList<String> pages = new ArrayList<>();
    PIXA images = lept.pixaReadMultipageTiff(savedPath.toString());
    //    lept.pixaReadMem

    for (int i = 0; i < images.n(); i++) {

      // Getting a page from whole file
      PIX image = images.pix(i);

      // Setting page to OCR
      tessBaseAPI.SetImage(image);

      // Get OCR result
      BytePointer outText = tessBaseAPI.GetUTF8Text();
      log.debug(
          "Extracted text from file "
              + savedPath.getFileName().toString()
              + " with size: "
              + outText.getString().length());
      System.out.println("Text extracted of length:\n" + outText.getString().length());

      pages.add(outText.getString());

      // Destroy used object and release memory
      outText.deallocate();
    }

    // Destroy used object and release memory
    lept.pixaDestroy(images);

    try {
      Files.deleteIfExists(savedPath);
    } catch (IOException e) {
      System.err.println("Problem with deleting file: " + e.getMessage());
      e.printStackTrace();
    }

    return new Document(savedPath.getFileName().toString(), "", pages);
  }

  private String extractTextFromPix(Path savedPath, String lang, Boolean highQuality) {}

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
