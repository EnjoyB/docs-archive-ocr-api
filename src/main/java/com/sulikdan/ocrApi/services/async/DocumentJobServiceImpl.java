package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
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
    Document document = ocrService.extractTextFromFile(filePath, lang, highQuality);
    System.out.println("Received document:" + document.toString());
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(filePath.getFileName().toString());

//    if (documentAsyncStatus == null || documentAsyncStatus.getDocumentProcessStatus() == DocumentProcessStatus.PROCESSING) {
      DocumentAsyncStatus newAsyncStatus = DocumentAsyncStatus.generateDocumentAsyncStatus(
              documentStorageService, DocumentProcessStatus.PROCESSING, filePath);

      //      synchronized (lock) {
      documentStorageService.getDocumentAsyncMap().put(filePath.getFileName().toString(), newAsyncStatus);
      //        documentAsyncMap.put(filePath.getFileName().toString(), newDef);
      //      }
//    }

    documentStorageService.getDocumentMap().put(filePath.getFileName().toString(), document);
  }
}
