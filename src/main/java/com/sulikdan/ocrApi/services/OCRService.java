package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRService is used for .....
 */
public interface OCRService {

  static final String PATH_TO_TESSDATA = "src/main/resources/tessdata";

  Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality);

  Document extractTextFromFile(
      MultipartFile file,
      String newFileName,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality);

  boolean addTesseractLanguage(String language);
  //  Document extractTextFromMultiPageTiff(Path savedPath, String lang, Boolean highQuality);
  //  Document extractTextFromMultiPageTiff(ByteAr ,Path savedPath, String lang, Boolean
  // highQuality);

  //  Document extractTextFromFile(Path savedPath, String lang, Boolean highQuality);


}
