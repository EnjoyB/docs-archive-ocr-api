package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.PDFService;
import org.springframework.scheduling.annotation.Async;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniel Šulik on 12-Jul-20
 *
 * <p>Class PDFJobWorker is used for .....
 */
@Async("threadPoolTaskExecutor")
public class PDFJobWorker implements Runnable {

  private final FileStorageService fileStorageService;
  private final OCRService ocrService;
  private final DocumentStorageService documentStorageService;
  private final PDFService pdfService;

  private final Path savedFilePath;
  private final String origFileName;

  private final String lang;
  private final Boolean multipageTiff;
  private final Boolean highQuality;

  public PDFJobWorker(
      FileStorageService fileStorageService,
      OCRService ocrService,
      DocumentStorageService documentStorageService,
      PDFService pdfService,
      Path savedFilePath,
      String origFileName,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
    this.documentStorageService = documentStorageService;
    this.pdfService = pdfService;
    this.savedFilePath = savedFilePath;
    this.origFileName = origFileName;
    this.lang = lang;
    this.multipageTiff = multipageTiff;
    this.highQuality = highQuality;
  }

  @Override
  public void run() {
    String fileNameOnServer = savedFilePath.getFileName().toString();

    List<Path> tmpPaths = pdfService.convertPDFToPNG(savedFilePath, origFileName);

    List<Document> pdfPages = new ArrayList<>();
    for (Path path : tmpPaths) {

      // OCR scanning
      pdfPages.add(
          ocrService.extractTextFromFile(path, origFileName, lang, multipageTiff, highQuality));
    }

    // Delete temp files
    tmpPaths.forEach(fileStorageService::deleteFile);

    // Merging One-page documents to one big multi-page document
    List<String> pages =
        pdfPages.stream()
            .map(Document::getPages)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    // Final Doc merged from scanned pages
    Document resultDoc =
        new Document(
            savedFilePath.getFileName().toString(), origFileName, pdfPages.get(0).getUrl(), pages);

    System.out.println("Received resultDoc:" + resultDoc.toString());

    // Look up the current resultDoc's status
    DocumentAsyncStatus documentAsyncStatus =
        documentStorageService.getDocumentAsyncMap().get(fileNameOnServer);

    // Setting new Status
    documentAsyncStatus.setDocumentProcessStatus(DocumentProcessStatus.COMPLETED);

    // Updating result to be available to requester
    documentStorageService.getDocumentMap().put(fileNameOnServer, resultDoc);

    // Deleting file
    fileStorageService.deleteFile(savedFilePath);
  }
}
