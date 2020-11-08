package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.DocumentService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import io.swagger.v3.oas.annotations.Operation;
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
 * <p>Class ImgDocumentController is used to process jpg, png, tiff files and scan them with the help of a OCR.
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("documents")
public class ImgDocumentController extends SharedControllerLogic {

  private final DocumentStorageService documentStorageService;
  private final DocumentService documentService;
  private final ObjectMapper mapper = new ObjectMapper();

  public ImgDocumentController(
      DocumentStorageService documentStorageService, DocumentService documentService) {
    this.documentStorageService = documentStorageService;
    this.documentService = documentService;
  }

  /**
   * A testing endpoint.
   * @return string that it shows its working.
   */
  @Operation(summary = "A testing endpoint.")
  @GetMapping(value = "/hello")
  public ResponseEntity<String> testController(){
    log.info("Its hello!");
    return ResponseEntity.status(HttpStatus.OK).body("Hello!\nIts working");
  }

  /**
   * Uploads file for scanning with asynchronous ability.
   * @param files
   * @param lang language of file/s
   * @param multiPageFile
   * @param highQuality
   * @return
   * @throws JsonProcessingException
   */
  @Operation(summary = "Uploads file for scanning with asynchronous ability.")
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

  /**
   * Deletes uploaded file from server.
   * @param fileName
   */
  @Operation(summary = "Deletes uploaded file from server.")
  @DeleteMapping("/{fileName}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteDocument(@PathVariable String fileName) {
    if (!StringUtils.isEmpty(fileName)) {
      documentService.deleteDocument(fileName);
    }
  }

  /**
   * Returns scanned solution for file.
   * @param fileName
   * @return results of scanning.
   * @throws JsonProcessingException
   */
  @Operation(summary = "Returns scanned solution for file.")
  @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getDocument(@PathVariable String fileName) throws JsonProcessingException {
    Document toRet = null;

    if (!StringUtils.isEmpty(fileName)) {
      toRet = documentService.getDocument(fileName);
    }

    if (toRet != null) {
      return ResponseEntity.ok(mapper.writeValueAsString(toRet));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Returns status of a document/file being processed.
   * @param fileName
   * @return status of a file
   * @throws JsonProcessingException
   */
  @Operation(summary = "Returns status of a document/file being processed.")
  @GetMapping(value = "/{fileName}/documentStatus", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getDocumentStatus(@PathVariable String fileName) throws JsonProcessingException {
    log.info("Called get status");
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(fileName);

    log.info("Searched file: " + fileName);
    if (documentAsyncStatus != null) {
      return ResponseEntity.ok(mapper.writeValueAsString(documentAsyncStatus));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Upload file for scanning and synchronously waits for result of scanning.
   * @param files
   * @param lang language
   * @param multiPageFile
   * @param highQuality
   * @return result of scanning
   * @throws JsonProcessingException
   */
  @Operation(summary = "Upload file for scanning and synchronously waits for result of scanning.")
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

}
