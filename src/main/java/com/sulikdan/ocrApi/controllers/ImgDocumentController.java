package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.async.DocumentJobWorker;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Šulik on 10-Jul-20
 *
 * <p>Class DocumentAsyncController is used for .....
 */
@Slf4j
@RestController
@RequestMapping("ocr/document")
public class ImgDocumentController extends SharedControllerLogic {

  private TaskExecutor taskExecutor;

  private final DocumentStorageService documentStorageService;
  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private final ObjectMapper mapper = new ObjectMapper();

  public ImgDocumentController(
      TaskExecutor taskExecutor,
      DocumentStorageService documentStorageService,
      OCRService ocrService,
      FileStorageService fileStorageService) {
    this.taskExecutor = taskExecutor;
    this.documentStorageService = documentStorageService;
    this.ocrService = ocrService;
    this.fileStorageService = fileStorageService;
  }

  //  @ResponseBody
  @PostMapping(
      value = "/test",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextAsyncTest(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang", defaultValue = "eng") String lang,
      @RequestParam(value = "multiPageFile", defaultValue = "false") Boolean multiPageFile,
      @RequestParam(value = "highQuality", defaultValue = "false") Boolean highQuality)
      throws JsonProcessingException {
    // TODO this is intended only for testign

    System.out.println("Went through");
    System.out.println(
        "AMount files: "
            + files.length
            + " - "
            + files[0].getName()
            + " - "
            + files[0].getContentType()
            + " - "
            + files[0].getOriginalFilename());
    System.out.println("lang: " + lang);
    System.out.println("multiPageFile: " + multiPageFile);
    System.out.println("highQuality: " + highQuality);

    DocumentAsyncStatus asyncStatus =
        DocumentAsyncStatus.builder()
            .documentProcessStatus(DocumentProcessStatus.PROCESSING)
            .currentStatusLink("yolooo")
            .resultLink("Topkek/.com")
            .build();

    return ResponseEntity.status(HttpStatus.OK)
        //        .body(mapper.writer().writeValueAsString(asyncStatus));
        .body(mapper.writeValueAsString(asyncStatus));
  }

  @ResponseBody
  @PostMapping(consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextAsync(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang", defaultValue = "eng") String lang,
      @RequestParam(value = "multiPageFile", defaultValue = "false") Boolean multiPageFile,
      @RequestParam(value = "highQuality", defaultValue = "false") Boolean highQuality)
      throws JsonProcessingException {

    checkSupportedLanguages(lang);

    List<DocumentAsyncStatus> documentAsyncStatusList = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedFilePath = fileStorageService.saveFile(file, generateNamePrefix());
      String savedFileName = savedFilePath.getFileName().toString();

      System.out.println("Async sending work to do!");
      DocumentAsyncStatus returnAsyncStatus =
          DocumentAsyncStatus.generateDocumentAsyncStatus(
              documentStorageService, DocumentProcessStatus.PROCESSING, savedFileName);

      taskExecutor.execute(
          new DocumentJobWorker(
              fileStorageService,
              ocrService,
              documentStorageService,
              savedFilePath,
              file.getOriginalFilename(),
              lang,
              multiPageFile,
              highQuality));

      documentAsyncStatusList.add(returnAsyncStatus);

      documentStorageService.getDocumentAsyncMap().put(savedFileName, returnAsyncStatus);
    }

    System.out.println("Finnishing in controller!");
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writeValueAsString(documentAsyncStatusList));
  }

  @DeleteMapping("/{fileName}")
  public void deleteDocument(@PathVariable String fileName) {

    if (documentStorageService.getDocumentMap().containsKey(fileName)) {
      documentStorageService.getDocumentMap().remove(fileName);
      documentStorageService.getDocumentAsyncMap().remove(fileName);
    }
  }

  @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getDocument(@PathVariable String fileName) throws JsonProcessingException {
    Document document = documentStorageService.getDocumentMap().get(fileName);

    if (document != null) {
      return mapper.writeValueAsString(document);
    } else {
      return mapper.writeValueAsString("null");
    }
  }

  @GetMapping(value = "/test")
  public String getDocs() throws JsonProcessingException {
    // TODO this is intended only for testign
    return mapper.writeValueAsString("YOLO");
  }

  @GetMapping(value = "/{fileName}/documentStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getDocumentStatus(@PathVariable String fileName) throws JsonProcessingException {
    log.info("Called get status");
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(fileName);

    log.info("Searched file: " + fileName);
    if (documentAsyncStatus != null) {
      return mapper.writeValueAsString(documentAsyncStatus);
    } else {
      return mapper.writeValueAsString("null");
    }
  }

  @ResponseBody
  @PostMapping(
      value = "/sync",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
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

      resultDocumentList.add(
          processFileSync(
              savedFilePath, file.getOriginalFilename(), lang, multiPageFile, highQuality));
      fileStorageService.deleteFile(savedFilePath);
    }

    return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(resultDocumentList));
  }

  private Document processFileSync(
      Path savedFilePath,
      String origFileName,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality) {
    return ocrService.extractTextFromFile(
        savedFilePath, origFileName, lang, multipageTiff, highQuality);
  }
}
