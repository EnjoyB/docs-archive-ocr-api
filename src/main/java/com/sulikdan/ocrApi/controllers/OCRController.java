package com.sulikdan.ocrApi.controllers;

import com.sulikdan.ocrApi.services.OCRService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRController is used for .....
 */
@RestController
@RequestMapping("ocr")
public class OCRController {

  OCRService ocrService;
  private MultipartFile file;
  private Boolean async;
  private Boolean highQuality;

  public OCRController(OCRService ocrService) {
    this.ocrService = ocrService;
  }

  @ResponseBody
  @PostMapping(path = "/extractText", consumes = "multipart/form-data")
  public ResponseEntity<String> uploadAndExtractText(
      @RequestPart("file") MultipartFile file,
      @RequestParam(value = "async") Optional<String> async,
      @RequestParam(value = "highQuality") Optional<String> highQuality) {

    ocrService.saveAndExtractText(file,highQuality.isPresent() ? Boolean.valueOf(highQuality.get()) : Boolean.valueOf(false));

    return new ResponseEntity<String>(HttpStatus.OK);

  }
}
