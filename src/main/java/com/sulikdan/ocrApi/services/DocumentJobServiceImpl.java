package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsync;
import com.sulikdan.ocrApi.entities.DocumentStatus;
import org.springframework.scheduling.annotation.Async;

import java.nio.file.Path;

/**
 * Created by Daniel Šulik on 09-Jul-20
 *
 * <p>Class DocumentManagerServiceImpl is used for .....
 */
@Async("threadPoolTaskExecutor")
// @Service
public class DocumentJobServiceImpl implements DocumentJobService {

  private FileStorageService fileStorageService;
  private OCRService ocrService;
  private DocumentStorageService documentStorageService;

  private Path filePath;
  private String lang;
  private Boolean highQuality;

  public DocumentJobServiceImpl(
      FileStorageService fileStorageService,
      OCRService ocrService,
      DocumentStorageService documentStorageService) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.documentStorageService = documentStorageService;
  }

  public DocumentJobServiceImpl(
      FileStorageService fileStorageService,
      OCRService ocrService,
      DocumentStorageService documentStorageService,
      Path filePath,
      String lang,
      Boolean highQuality) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.documentStorageService = documentStorageService;
    this.filePath = filePath;
    this.lang = lang;
    this.highQuality = highQuality;
  }

  @Override
  public void setJobParams(Path filePath, String lang, Boolean highQuality) {
    this.filePath = filePath;
    this.lang = lang;
    this.highQuality = highQuality;
  }

  @Override
  public void run() {
    Document document = ocrService.extractTextFromFile(filePath.toString(), lang, highQuality);
    System.out.println("Received document:" + document.toString());
    DocumentAsync documentAsync =
        documentStorageService.getDocumentAsyncMap().get(filePath.getFileName().toString());

    if (documentAsync == null || documentAsync.getDocumentStatus() == DocumentStatus.PROCESSING) {
      DocumentAsync newDef =
          DocumentAsync.builder()
              .currentStatusLink(
                  documentStorageService.getGetDocumentAsyncUri()
                      + filePath.getFileName().toString())
              .documentStatus(DocumentStatus.COMPLETED)
              .currentStatusLink(
                  documentStorageService.getGetDocumentUri() + filePath.getFileName().toString())
              .build();
      //      synchronized (lock) {
      documentStorageService.getDocumentAsyncMap().put(filePath.getFileName().toString(), newDef);
      //        documentAsyncMap.put(filePath.getFileName().toString(), newDef);
      //      }
    }

    documentStorageService.getDocumentMap().put(filePath.getFileName().toString(), document);
  }
}
