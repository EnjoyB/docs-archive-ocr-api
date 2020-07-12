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
  private final ObjectMapper mapper = new ObjectMapper();

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
  }

  @ResponseBody
  @PostMapping(consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextSync(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang", defaultValue = "eng") String lang,
      @RequestParam(value = "multiPageFile", defaultValue = "false") Boolean multiPageFile,
      @RequestParam(value = "highQuality", defaultValue = "false") Boolean highQuality)
      throws JsonProcessingException {

    checkSupportedLanguages(lang);

    List<Document> resultDocumentList = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedFilePath = fileStorageService.saveFile(file, generateNamePrefix());

      resultDocumentList.add(processFileSync(savedFilePath, file.getOriginalFilename(), lang, multiPageFile, highQuality));
      fileStorageService.deleteFile(savedFilePath);
    }

    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultDocumentList));
  }

  // TODO delete after refactoring!!
  private Document processFileSync(Path filePath, String lang, Boolean highQuality) {
    return ocrService.extractTextFromFile(null, "filePath", lang, false, highQuality);
  }

  private Document processFileSync(
      Path savedFilePath, String origFileName, String lang, Boolean multipageTiff, Boolean highQuality) {
    return ocrService.extractTextFromFile(savedFilePath, origFileName, lang, multipageTiff, highQuality);
  }

  private static String generateUriForAsyncStatus(
      Path filePath, String methodName, String methodUri) {
    return MvcUriComponentsBuilder.fromController(ImgDocumentController.class).toString()
        + methodUri
        + filePath.getFileName().toString();
  }
}
