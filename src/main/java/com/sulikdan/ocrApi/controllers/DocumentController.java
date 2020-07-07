package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRController is used for .....
 */
// @Profile("backUp")
@RestController
@RequestMapping("ocr")
public class DocumentController {

  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private MultipartFile file;
  private Boolean async;
  private Boolean highQuality;
  private ObjectMapper objectMapper;

  public DocumentController(OCRService ocrService, FileStorageService fileStorageService) {
    this.ocrService = ocrService;
    this.fileStorageService = fileStorageService;
    objectMapper = new ObjectMapper();
  }

  @ResponseBody
  @PostMapping(
      path = "/document",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractText(
      @RequestPart("file") MultipartFile file,
      @RequestParam(value = "lang") Optional<String> lang,
      @RequestParam(value = "async") Optional<String> async,
      @RequestParam(value = "highQuality") Optional<String> highQuality) {

    // TODO file extension/format check
    // Works with single documents with multi documents, will be problem
    Document extracted = null;
    try {
      extracted =
          ocrService.saveAndExtractText(
              file,
              lang.map(String::toLowerCase).orElse("eng"),
              highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE));
    } catch (TesseractException e) {
      System.out.println("Error from tesseract!");
      e.printStackTrace();
    }

    try {
      return new ResponseEntity<String>(objectMapper.writeValueAsString(extracted), HttpStatus.OK);
    } catch (JsonProcessingException e) {
      // TODo fix error response
      e.printStackTrace();
      return new ResponseEntity<String>("error", HttpStatus.BAD_REQUEST);
    }
  }

  @ResponseBody
  @PostMapping(
      path = "/documents",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractText(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang") Optional<String> lang,
      @RequestParam(value = "async") Optional<String> async,
      @RequestParam(value = "highQuality") Optional<String> highQuality) {

    // TODO file extension/format check
    // Works with single documents with multi documents, will be problem
    List<Document> documentList = new ArrayList<>();

    for (MultipartFile file : files) {

      try {
        Document extracted =
            ocrService.saveAndExtractText(
                file,
                lang.map(String::toLowerCase).orElse("eng"),
                highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE));
        documentList.add(extracted);
      } catch (TesseractException e) {
        System.out.println("Error from tesseract!");
        e.printStackTrace();
      }
    }

    try {
      //      return objectMapper.writeValueAsString(extracted);
      return new ResponseEntity<String>(
          objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentList),
          HttpStatus.OK);
    } catch (JsonProcessingException e) {
      // TODo fix error response
      e.printStackTrace();
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/document/{fileName}")
  public void deleteDocument(@PathVariable String fileName) {
    //    This should be for async
    //    fi
  }

  @GetMapping("/document/{fileName}")
  public String getDocument(@PathVariable String fileName) {
    //    This should be for async
    return "null";
  }

  /**
   * Simple check supported languages. There are also other languages, but 1st they need to be added
   * here and then make sure that correct tesseract dataset is in folder.
   *
   * @param language expecting string in lower-case
   * @return true if it equals to specified/selected languages
   */
  private boolean checkSupportedLanguages(String language) {
    switch (language) {
      case "eng":
      case "cz":
      case "svk":
        return true;
      default:
        return false;
    }
  }
}
