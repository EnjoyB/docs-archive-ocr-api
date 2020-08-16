package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.OcrApiApplication;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.OcrConfig;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.PIXA;
import org.bytedeco.leptonica.global.lept;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
      Path savedFilePath, String origFileName, OcrConfig ocrConfig) {

    if (savedFilePath.toString().length() <= 0)
      throw new RuntimeException("File not here or WrongFile");
    // TODO

    Document extractedFile =
        new Document(savedFilePath.getFileName().toString(), origFileName, "", new ArrayList<>());

    if (ocrConfig.getMultiPages()) {
      PIXA images = lept.pixaReadMultipageTiff(savedFilePath.toString());

      for (int i = 0; i < images.n(); i++) {

        String scannedPage =
            extractTextFromPix(images.pix(i), savedFilePath.getFileName().toString(), ocrConfig);
        extractedFile.getPages().add(scannedPage);
      }

      // free resources
      lept.pixaDestroy(images);

    } else {
      PIX image = lept.pixRead(savedFilePath.toString());
      String scannedPage =
          extractTextFromPix(image, savedFilePath.getFileName().toString(), ocrConfig);
      extractedFile.getPages().add(scannedPage);

      // free resources
      lept.pixDestroy(image);
    }

    return extractedFile;
  }

  private String extractTextFromPix(PIX image, String newFileName, OcrConfig ocrConfig) {

    if (!addTesseractLanguage(ocrConfig.getLang())) {
      // TODO return error - unsupported language!
      return null;
    }

    // this is not going to work out with multiple threads ...
    // TODO recreate new instance for every file?
    TessBaseAPI tessBaseAPI = byLanguageTPlatform.get(ocrConfig.getLang());
    //    TessBaseAPI tessBaseAPI = CreateNewInstanceOfTessBaseAPI(lang);
    //    Don't forget on every new instance to delete it!!!! TODO

    // Setting page to OCR
    tessBaseAPI.SetImage(image);

    // Get OCR result
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    log.debug(
        "Extracted text from file " + newFileName + " with size: " + outText.getString().length());
    System.out.println("Text extracted of length:\n" + outText.getString().length());

    String outputString = outText.getString();

    // Destroy used object and release memory
    outText.deallocate();

    return outputString;
  }

  @Override
  public boolean addTesseractLanguage(String language) {
    if (byLanguageTPlatform.containsKey(language)) return true;

    TessBaseAPI newTessBaseAPI = new TessBaseAPI();

    if (newTessBaseAPI.Init(OcrApiApplication.pathToTessdata, language) != 0) {
      System.err.println("Could not initialize tesseract, with language: " + language);
      return false;
    }
    byLanguageTPlatform.put(language, newTessBaseAPI);
    return true;
  }

  /**
   * Need to be tweaked with queue containing available instances? to avoid recreate them!!! TODO !!
   *
   * @param language
   * @return
   */
  public TessBaseAPI CreateNewInstanceOfTessBaseAPI(String language) {

    TessBaseAPI newTessBaseAPI = new TessBaseAPI();

    if (newTessBaseAPI.Init(OcrApiApplication.pathToTessdata, language) != 0) {
      System.err.println("Could not initialize tesseract, with language: " + language);
      throw new RuntimeException(
          "Couldn't init another TessBaseAPI instance with language: " + language);
    }

    return newTessBaseAPI;
  }
}
