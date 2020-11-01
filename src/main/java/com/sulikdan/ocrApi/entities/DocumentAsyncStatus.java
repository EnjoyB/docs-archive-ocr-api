package com.sulikdan.ocrApi.entities;

import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import lombok.*;

import java.nio.file.Path;


/**
 * Created by Daniel Šulik on 08-Jul-20
 *
 * <p>Class DocumentStatus is an entiny containing async. status of a document to be scanned.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAsyncStatus {

  /**
   * Represents current status of the document.
   */
  private DocumentProcessStatus documentProcessStatus;

  /**
   * A link/url to resource containing status of document.
   */
  private String currentStatusLink;

  /**
   * A link/url to resource containing processed/scanned document.
   */
  private String resultLink;


  /**
   * A method to generate DocumentAsyncStatus consisting of provided params.
   * @param service for getting uri to corrent API mapping
   * @param processStatus new processStatus of OCR to corresponding document
   * @param newFileName string representing new file name assignet to file to avoid collision with same names
   * @return
   */
  public static DocumentAsyncStatus generateDocumentAsyncStatus(
          DocumentStorageService service, DocumentProcessStatus processStatus, String newFileName) {
    return DocumentAsyncStatus.builder()
            .documentProcessStatus(processStatus)
            .currentStatusLink(service.getGetDocumentUri() + newFileName + "/documentStatus")
            .resultLink(service.getGetDocumentUri() + newFileName)
            .build();
  }
}
