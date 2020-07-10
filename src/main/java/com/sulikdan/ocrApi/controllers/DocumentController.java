package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsync;
import com.sulikdan.ocrApi.entities.DocumentStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRController is used for .....
 */
// @Profile("backUp")
@RestController
@RequestMapping("ocr")
public class DocumentController {

  @Autowired private TaskExecutor taskExecutor;

  private final OCRService ocrService;
  private final FileStorageService fileStorageService;
  private ObjectMapper mapper;
  private final Object lock = new Object();

  // For async communication
  public static final HashMap<String, Document> documentMap = new HashMap<>();
  public static final HashMap<String, DocumentAsync> documentAsyncMap = new HashMap<>();

//  public static final String getDocumentMapping =
//      MvcUriComponentsBuilder.fromMethodName(DocumentController.class, "getDocument").build().toString();
//  public static final String getDocumentStatusMapping =
//      MvcUriComponentsBuilder.fromMethodName(DocumentController.class, "getDocumentStatus").build()
//          .toString();

  public DocumentController(
      TaskExecutor taskExecutor, OCRService ocrService, FileStorageService fileStorageService) {
    this.taskExecutor = taskExecutor;
    this.ocrService = ocrService;
    this.fileStorageService = fileStorageService;
    //    this.taskExecutor = ;
    mapper = new ObjectMapper();
  }

  @ResponseBody
  @PostMapping(
      path = "/sync/document",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
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

  @ResponseBody
  @PostMapping(
      path = "/async/document",
      consumes = "multipart/form-data",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> uploadAndExtractTextAsync(
      @RequestPart("files") MultipartFile[] files,
      @RequestParam(value = "lang") Optional<String> lang,
      @RequestParam(value = "highQuality") Optional<String> highQuality)
      throws JsonProcessingException {

    // TODO file extension/format check
    // Works with single documents with multi documents, will be problem
    List<DocumentAsync> documentAsyncList = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedPath = fileStorageService.saveFile(file);

      System.out.println("Async sending work to do!");
      DocumentAsync returnStatus =
          DocumentAsync.builder()
              .documentStatus(DocumentStatus.PROCESSING)
              .currentStatusLink(generateUriForAsyncStatus(savedPath, "getDocumentStatus", ""))
              .resultLink(generateUriForAsyncStatus(savedPath, "getDocument", ""))
              .build();

      //      taskExecutor.execute();
      //      asyncExecutor().execute(processFileAsync(
      //              savedPath,
      //              lang.map(String::toLowerCase).orElse("eng"),
      //              highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE)));
      //      try {
      taskExecutor.execute(
//          new DocumentJob(
          new DocumentJob(
              fileStorageService,
              ocrService,
              savedPath,
              lang.map(String::toLowerCase).orElse("eng"),
              highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE)));
      //                processFileAsync(
      //                    savedPath,
      //                    lang.map(String::toLowerCase).orElse("eng"),
      //                    highQuality.map(Boolean::valueOf).orElseGet(() -> Boolean.FALSE));
      //
      //      } catch (ExecutionException e) {
      //        e.printStackTrace();
      //      } catch (InterruptedException e) {
      //        e.printStackTrace();
      //      }
      //      CompletableFuture.

      documentAsyncList.add(returnStatus);
      synchronized (lock) {
        documentAsyncMap.put(savedPath.getFileName().toString(), returnStatus);
      }
    }

    System.out.println("Finnishing in controller!");
    return ResponseEntity.status(HttpStatus.OK)
        .body(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentAsyncList));
  }

  @DeleteMapping("/document/{fileName}")
  public void deleteDocument(@PathVariable String fileName) {
    //    This should be for async
    //    fi
  }

  @GetMapping("/document/{fileName}")
  public String getDocument(@PathVariable String fileName) throws JsonProcessingException {
    Document document = documentMap.get(fileName);
    if (document != null) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(document);
    }
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString("null");
  }

  @GetMapping("/documentStatus/{fileName}")
  public String getDocumentStatus(@PathVariable String fileName) throws JsonProcessingException {
    DocumentAsync documentAsync = documentAsyncMap.get(fileName);
    if (documentAsync != null) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentAsync);
    }
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString("null");
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

  private Document processFileSync(String filePath, String lang, Boolean highQuality) {
    return ocrService.extractTextFromFile(filePath, lang, highQuality);
  }

  @Async("threadPoolTaskExecutor")
  public CompletableFuture<Void> processFileAsync(Path filePath, String lang, Boolean highQuality)
      throws ExecutionException, InterruptedException {

    Document document = ocrService.extractTextFromFile(filePath.toString(), lang, highQuality);
    System.out.println("Received document:" + document.toString());
    DocumentAsync documentAsync = documentAsyncMap.get(filePath.getFileName().toString());

    if (documentAsync == null || documentAsync.getDocumentStatus() == DocumentStatus.PROCESSING) {
      DocumentAsync newDef =
          DocumentAsync.builder()
//              .currentStatusLink(generateUriForAsyncStatus(filePath, "getDocumentStatus", ""))
              .currentStatusLink(MvcUriComponentsBuilder.fromMappingName("/ocr/async/documentStatus/{filename}").build().toString())
              .documentStatus(DocumentStatus.COMPLETED)
              .resultLink(MvcUriComponentsBuilder.fromMappingName("/ocr/async/document/{filename}").build().toString())
              .build();
      synchronized (lock) {
        documentAsyncMap.put(filePath.getFileName().toString(), newDef);
      }
    }

    documentMap.put(filePath.getFileName().toString(), document);
    return CompletableFuture.completedFuture(null);
  }

  private static String generateUriForAsyncStatus(
      Path filePath, String methodName, String methodUri) {
    return MvcUriComponentsBuilder.fromController(DocumentController.class).toString()
        + methodUri
        + filePath.getFileName().toString();
    //    return MvcUriComponentsBuilder.fromMethodName(
    //            DocumentController.class, methodName, filePath.getFileName().toString())
    //        .build()
    //        .toString();

    //    return MvcUriComponentsBuilder.fromMethodCall(on(DocumentController.class).getDocument())
  }

//  @Async("threadPoolTaskExecutor")
//  private class DocumentJob implements Runnable {
//
//    private FileStorageService fileStorageService;
//    private OCRService ocrService;
//    private Path filePath;
//    private String lang;
//    private Boolean highQuality;
//
//    public DocumentJob(
//        FileStorageService fileStorageService,
//        OCRService ocrService,
//        Path filePath,
//        String lang,
//        Boolean highQuality) {
//      this.fileStorageService = fileStorageService;
//      this.ocrService = ocrService;
//      this.filePath = filePath;
//      this.lang = lang;
//      this.highQuality = highQuality;
//    }
//
//    @Override
//    public void run() {
//      Document document = ocrService.extractTextFromFile(filePath.toString(), lang, highQuality);
//      System.out.println("Received document:" + document.toString());
//      DocumentAsync documentAsync = documentAsyncMap.get(filePath.getFileName().toString());
//
//      if (documentAsync == null || documentAsync.getDocumentStatus() == DocumentStatus.PROCESSING) {
//        DocumentAsync newDef =
//            DocumentAsync.builder()
//                         .currentStatusLink(MvcUriComponentsBuilder.fromMappingName("/ocr/async/document/{filename}").build().toString())
//                .documentStatus(DocumentStatus.COMPLETED)
//                         .currentStatusLink(MvcUriComponentsBuilder.fromMappingName("/ocr/async/documentStatus/{filename}").build().toString())
//                .build();
//        synchronized (lock) {
//          documentAsyncMap.put(filePath.getFileName().toString(), newDef);
//        }
//      }
//
//      documentMap.put(filePath.getFileName().toString(), document);
//    }
//  }
}
