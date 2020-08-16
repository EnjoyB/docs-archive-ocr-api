package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.PDFService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import com.sulikdan.ocrApi.services.async.PDFJobWorker;
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
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class PDFController is used for .....
 */
@RestController
@RequestMapping("ocr/pdf")
public class PDFController extends SharedControllerLogic {

  private TaskExecutor taskExecutor;

  private final DocumentStorageService documentStorageService;
  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private final PDFService pdfService;

  private final ObjectMapper mapper = new ObjectMapper();

  public PDFController(
      TaskExecutor taskExecutor,
      DocumentStorageService documentStorageService,
      OCRService ocrService,
      FileStorageService fileStorageService,
      PDFService pdfService) {
    this.taskExecutor = taskExecutor;
    this.documentStorageService = documentStorageService;
    this.ocrService = ocrService;
    this.fileStorageService = fileStorageService;
    this.pdfService = pdfService;
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
          new PDFJobWorker(
              fileStorageService,
              ocrService,
              documentStorageService,
              pdfService,
              savedFilePath,
              file.getOriginalFilename(),
              lang,
              multiPageFile,
              highQuality));

      documentAsyncStatusList.add(returnAsyncStatus);

      documentStorageService.getDocumentAsyncMap().put(savedFileName, returnAsyncStatus);
    }

    System.out.println("Finnishing in PDF async controller!");
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentAsyncStatusList));
  }

  @DeleteMapping("/{fileName}")
  public void deleteDocument(@PathVariable String fileName) {

    if (documentStorageService.getDocumentMap().containsKey(fileName)) {
      documentStorageService.getDocumentMap().remove(fileName);
      documentStorageService.getDocumentAsyncMap().remove(fileName);
    }
  }

  @GetMapping("/{fileName}")
  public String getDocument(@PathVariable String fileName) throws JsonProcessingException {
    Document document = documentStorageService.getDocumentMap().get(fileName);

    if (document != null) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    } else {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString("null");
    }
  }

  @GetMapping("/{fileName}/documentStatus")
  public String getDocumentStatus(@PathVariable String fileName) throws JsonProcessingException {
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(fileName);

    if (documentAsyncStatus != null) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentAsyncStatus);
    } else {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString("null");
    }
  }
}
