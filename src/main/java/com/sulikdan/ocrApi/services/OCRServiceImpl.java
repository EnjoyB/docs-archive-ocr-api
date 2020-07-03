package com.sulikdan.ocrApi.services;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.bytedeco.leptonica.global.lept.pixDestroy;
import static org.bytedeco.leptonica.global.lept.pixRead;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRServiceImpl is used for .....
 */
@Service
public class OCRServiceImpl implements OCRService {

  private final FileStorageService fileStorageService;

  public OCRServiceImpl(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  @Override
  public String saveAndExtractText(MultipartFile file, Boolean highQuality) {
    String savedPath = fileStorageService.saveFile(file);

    if (savedPath.length() <= 0) throw new RuntimeException("File not here");

    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    if (tessBaseAPI.Init(null, "eng") != 0) {
      System.err.println("Could not initialize tesseract.");
      System.exit(1);
    }

    PIX image = pixRead(savedPath);

    tessBaseAPI.SetImage(image);


    // Get OCR result
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    System.out.println("OCR output:\n" + outText.getString());

    // Destroy used object and release memory
    tessBaseAPI.End();
    outText.deallocate();
    pixDestroy(image);

    return outText.getString();
  }
}
