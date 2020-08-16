package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
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
public class DocumentJobWorker implements Runnable {

  private final FileStorageService fileStorageService;
  private final OCRService ocrService;
  private final DocumentStorageService documentStorageService;

  private final Path savedFilePath;
  private final String origFileName;

  private final OcrConfig ocrConfig;

  public DocumentJobWorker(
      FileStorageService fileStorageService,
      OCRService ocrService,
      DocumentStorageService documentStorageService,
      Path savedFilePath,
      String origFileName,
      OcrConfig ocrConfig) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.documentStorageService = documentStorageService;
    this.savedFilePath = savedFilePath;
    this.origFileName = origFileName;
    this.ocrConfig = ocrConfig;
  }

  @Override
  public void run() {
    String fileNameOnServer = savedFilePath.getFileName().toString();

    // Extracting data from file
    Document resultDoc = ocrService.extractTextFromFile(savedFilePath, origFileName, ocrConfig);

    System.out.println("Received resultDoc:" + resultDoc.toString());

    // Look up the current resultDoc's status
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(fileNameOnServer);

    // Setting new Status
    documentAsyncStatus.setDocumentProcessStatus(DocumentProcessStatus.SCANNED);

    // Updating result to be available to requester
    documentStorageService.getDocumentMap().put(fileNameOnServer, resultDoc);

    // Deleting file
    fileStorageService.deleteFile(savedFilePath);
  }
}
