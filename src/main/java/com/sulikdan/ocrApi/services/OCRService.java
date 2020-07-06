package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRService is used for .....
 */
public interface OCRService {

  Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality)
      throws TesseractException;

  boolean addTesseractLanguage(String language);
}
