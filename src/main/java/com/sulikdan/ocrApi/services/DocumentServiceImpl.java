package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.async.DocumentJobWorker;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Daniel Šulik on 16-Aug-20
 *
 * <p>Class DocumentServiceImpl is used for .....
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

  private final DocumentStorageService documentStorageService;
  private final FileStorageService fileStorageService;
  private final OCRService ocrService;
  private final TaskExecutor taskExecutor;

  public DocumentServiceImpl(
      DocumentStorageService documentStorageService,
      FileStorageService fileStorageService,
      OCRService ocrService,
      TaskExecutor taskExecutor) {
    this.documentStorageService = documentStorageService;
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.taskExecutor = taskExecutor;
  }

  @Override
  public void deleteDocument(String fileName) {
    if (documentStorageService.getDocumentMap().containsKey(fileName)) {
      documentStorageService.getDocumentMap().remove(fileName);
      documentStorageService.getDocumentAsyncMap().remove(fileName);
    }
  }

  @Override
  public Document getDocument(String fileName) {
    return documentStorageService.getDocumentMap().get(fileName);
  }

  @Override
  public List<DocumentAsyncStatus> processDocuments(MultipartFile[] files, OcrConfig ocrConfig) {

    List<DocumentAsyncStatus> documentsStatus = new ArrayList<>();

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
              ocrConfig));

      documentsStatus.add(returnAsyncStatus);

      documentStorageService.getDocumentAsyncMap().put(savedFileName, returnAsyncStatus);
    }

    return documentsStatus;
  }

  @Override
  public List<Document> processDocumentsSync(MultipartFile[] files, OcrConfig ocrConfig) {
    List<Document> docsScanned = new ArrayList<>();

    for (MultipartFile file : files) {
      Path savedFilePath = fileStorageService.saveFile(file, generateNamePrefix());

      docsScanned.add(processFileSync(savedFilePath, file.getOriginalFilename(), ocrConfig));
      fileStorageService.deleteFile(savedFilePath);
    }
    return docsScanned;
  }

  private Document processFileSync(Path savedFilePath, String origFileName, OcrConfig ocrConfig) {
    return ocrService.extractTextFromFile(savedFilePath, origFileName, ocrConfig);
  }

  /**
   * Generates name prefix for uploaded files. consisting of OCR_Timestamp
   *
   * @return strings "OCR_" + "current_timestamp_now()"
   * @implNote It's temporary solution and for many threaded usage, there may chance of collision
   *     and may need to be tweaked with adding thread number to it.
   */
  protected static String generateNamePrefix() {
    Date now = new Date();
    return "OCR_" + now.getTime();
  }
}
