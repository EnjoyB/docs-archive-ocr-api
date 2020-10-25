package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.DocumentService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Daniel Šulik on 10-Jul-20
 *
 * <p>Class DocumentAsyncController is used for .....
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("ocr/document")
public class ImgDocumentController extends SharedControllerLogic {

  private final DocumentStorageService documentStorageService;
  private final DocumentService documentService;
  private final ObjectMapper mapper = new ObjectMapper();

  public ImgDocumentController(
      DocumentStorageService documentStorageService, DocumentService documentService) {
    this.documentStorageService = documentStorageService;
    this.documentService = documentService;
  }

  @GetMapping(value = "/hello")
  public ResponseEntity<String> testController(){
    log.info("Its hello!");
    return ResponseEntity.status(HttpStatus.OK).body("Hello!\nIts working");
  }

  @ResponseBody
  @PostMapping(consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextAsync(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang", defaultValue = "eng") String lang,
      @RequestParam(value = "multiPageFile", defaultValue = "false") Boolean multiPageFile,
      @RequestParam(value = "highQuality", defaultValue = "false") Boolean highQuality)
      throws JsonProcessingException {
    log.info("Inside uploading!");
    List<DocumentAsyncStatus> documentAsyncStatusList = null;
    try{


    checkSupportedLanguages(lang);
    OcrConfig ocrConfig =
        OcrConfig.builder().lang(lang).multiPages(multiPageFile).highQuality(highQuality).build();
    documentAsyncStatusList =
        documentService.processDocuments(files, ocrConfig);
    } catch (Exception e){
      log.error("Something fucked up!\n" +  e.getMessage());
      e.getStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body(mapper.writeValueAsString("Troubles ..."));
    }

    log.info("Finnishing in async controller!");
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writeValueAsString(documentAsyncStatusList));
  }

  @DeleteMapping("/{fileName}")
  public void deleteDocument(@PathVariable String fileName) {
    if (!StringUtils.isEmpty(fileName)) {
      documentService.deleteDocument(fileName);
    }
  }

  @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String getDocument(@PathVariable String fileName) throws JsonProcessingException {
    Document toRet = null;

    if (!StringUtils.isEmpty(fileName)) {
      toRet = documentService.getDocument(fileName);
    }

    if (toRet != null) {
      return mapper.writeValueAsString(toRet);
    } else {
      return mapper.writeValueAsString("null");
    }
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
    OcrConfig ocrConfig =
            OcrConfig.builder().lang(lang).multiPages(multiPageFile).highQuality(highQuality).build();

    List<Document> resultDocumentList = documentService.processDocumentsSync(files, ocrConfig);

    return ResponseEntity.status(HttpStatus.OK).body(mapper.writeValueAsString(resultDocumentList));
  }

  // TODO test part
  // TODO test Part
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
}
