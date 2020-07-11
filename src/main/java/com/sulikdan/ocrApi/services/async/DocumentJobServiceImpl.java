package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;


/**
 * Created by Daniel Šulik on 09-Jul-20
 *
 * <p>Class DocumentManagerServiceImpl is used for .....
 */
@Async("threadPoolTaskExecutor")
// @Service
public class DocumentJobServiceImpl implements DocumentJobService {

  private final FileStorageService fileStorageService;
  private final OCRService ocrService;
  private final DocumentStorageService documentStorageService;

  private final MultipartFile file;
  private final String newFilePrefix;

  private final String lang;
  private final Boolean multipageTiff;
  private final Boolean highQuality;

  public DocumentJobServiceImpl(
      FileStorageService fileStorageService,
      OCRService ocrService,
      DocumentStorageService documentStorageService,
      MultipartFile file,
      String newFilePrefix,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.documentStorageService = documentStorageService;
    this.file = file;
    this.newFilePrefix = newFilePrefix;
    this.lang = lang;
    this.multipageTiff = multipageTiff;
    this.highQuality = highQuality;
  }


  @Override
  public void run() {
    String newFileNameForMapping = newFilePrefix + file.getOriginalFilename();

    // Extracting data from file
    Document resultDoc =
        ocrService.extractTextFromFile(
            file, newFileNameForMapping, lang, multipageTiff, highQuality);

    System.out.println("Received resultDoc:" + resultDoc.toString());

    // Look up the current resultDoc's status
    DocumentAsyncStatus documentAsyncStatus = documentStorageService.getDocumentAsyncMap().get(newFileNameForMapping);

    // Generating new Status
    DocumentAsyncStatus newAsyncStatus =
        DocumentAsyncStatus.generateDocumentAsyncStatus(
            documentStorageService, DocumentProcessStatus.PROCESSING, newFileNameForMapping);

    // Creating new Satus for requester know about status
    documentStorageService
        .getDocumentAsyncMap()
        .put(newFileNameForMapping, newAsyncStatus);

    // Updating result to be available to requester
    documentStorageService.getDocumentMap().put(newFileNameForMapping, resultDoc);
  }
}
