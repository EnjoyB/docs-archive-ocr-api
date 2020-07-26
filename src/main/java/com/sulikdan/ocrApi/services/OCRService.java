package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRService is used for .....
 * Inspired from https://medium.com/gft-engineering/creating-an-ocr-microservice-using-tesseract-pdfbox-and-docker-155beb7f2623
 */
public interface OCRService {

  static final String PATH_TO_TESSDATA_DEV = "src/main/r999esources/tessdata";
//  static final String PATH_TO_TESSDATA_DOCKER = "/usr/share/tessdata";
  static final String PATH_TO_TESSDATA = PATH_TO_TESSDATA_DEV;
//  static String PATH_TO_TESSDATA ;


  Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality);

  Document extractTextFromFile(
      Path savedFilePath,
      String origFileName,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality);

  boolean addTesseractLanguage(String language);
  //  Document extractTextFromMultiPageTiff(Path savedPath, String lang, Boolean highQuality);
  //  Document extractTextFromMultiPageTiff(ByteAr ,Path savedPath, String lang, Boolean
  // highQuality);

  //  Document extractTextFromFile(Path savedPath, String lang, Boolean highQuality);


}
