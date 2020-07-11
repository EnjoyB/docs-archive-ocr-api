package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import org.springframework.boot.context.properties.bind.DefaultValue;
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

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRController is used for .....
 */
// @Profile("backUp")
@RestController
@RequestMapping("ocr/sync/document")
public class ImgDocumentController extends SharedControllerLogic {

  private final DocumentStorageService documentStorageService;
  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private ObjectMapper mapper;
  //  private final Object lock = new Object();

  // For async communication
  public static final HashMap<String, Document> documentMap = new HashMap<>();
  public static final HashMap<String, DocumentAsyncStatus> documentAsyncMap = new HashMap<>();

  public ImgDocumentController(
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
      @DefaultValue("eng") @RequestParam(value = "lang") String lang,
      @DefaultValue("false") @RequestParam(value = "multiPageFile") Boolean multiPageFile,
      @DefaultValue("true") @RequestParam(value = "highQuality") Boolean highQuality)
      throws JsonProcessingException {

    // TODO file extension/format check
    // Works with single documents with multi documents, will be problem
    checkSupportedLanguages(lang);

    List<Document> resultDocumentList = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedPath = fileStorageService.saveFile(file);

      resultDocumentList.add(processFileSync(savedPath, lang, highQuality));
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDocumentList));
  }

  private Document processFileSync(Path filePath, String lang, Boolean highQuality) {
    return ocrService.extractTextFromFile(filePath, lang, highQuality);
  }



  private static String generateUriForAsyncStatus(
      Path filePath, String methodName, String methodUri) {
    return MvcUriComponentsBuilder.fromController(ImgDocumentController.class).toString()
        + methodUri
        + filePath.getFileName().toString();
  }
}
