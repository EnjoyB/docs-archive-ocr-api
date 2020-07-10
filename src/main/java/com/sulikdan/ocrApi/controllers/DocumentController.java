package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsync;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRController is used for .....
 */
// @Profile("backUp")
@RestController
@RequestMapping("ocr/document")
public class DocumentController {

  private final DocumentStorageService documentStorageService;
  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private ObjectMapper mapper;
  private final Object lock = new Object();

  // For async communication
  public static final HashMap<String, Document> documentMap = new HashMap<>();
  public static final HashMap<String, DocumentAsync> documentAsyncMap = new HashMap<>();

  public DocumentController(
      DocumentStorageService documentStorageService,
      OCRService ocrService,
      FileStorageService fileStorageService) {
    this.documentStorageService = documentStorageService;
    this.ocrService = ocrService;
    this.fileStorageService = fileStorageService;

    //    this.taskExecutor = ;
    mapper = new ObjectMapper();
  }

  @ResponseBody
  @PostMapping(consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextSync(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang") Optional<String> lang,
      @RequestParam(value = "highQuality") Optional<String> highQuality)
      throws JsonProcessingException {

    // TODO file extension/format check
    // Works with single documents with multi documents, will be problem

    List<Document> resultDocumentList = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedPath = fileStorageService.saveFile(file);

      resultDocumentList.add(
          processFileSync(
              savedPath.toString(),
              lang.map(String::toLowerCase).orElse("eng"),
              highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE)));
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDocumentList));
  }

  /**
   * Simple check supported languages. There are also other languages, but 1st they need to be added
   * here and then make sure that correct tesseract dataset is in folder.
   *
   * @param language expecting string in lower-case
   * @return true if it equals to specified/selected languages
   */
  public static boolean checkSupportedLanguages(String language) {
    switch (language) {
      case "eng":
      case "cz":
      case "svk":
        return true;
      default:
        return false;
    }
  }

  private Document processFileSync(String filePath, String lang, Boolean highQuality) {
    return ocrService.extractTextFromFile(filePath, lang, highQuality);
  }

  private static String generateUriForAsyncStatus(
      Path filePath, String methodName, String methodUri) {
    return MvcUriComponentsBuilder.fromController(DocumentController.class).toString()
        + methodUri
        + filePath.getFileName().toString();
  }
}
