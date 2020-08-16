package com.sulikdan.ocrApi.entities;

import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

/**
 * Created by Daniel Šulik on 08-Jul-20
 *
 * <p>Class DocumentStatus is used for .....
 */
@Getter
@Setter
@Builder
public class DocumentAsyncStatus {

  private DocumentProcessStatus documentProcessStatus;
  private String currentStatusLink;
  private String resultLink;



  public DocumentAsyncStatus() {}

  public DocumentAsyncStatus(DocumentProcessStatus documentProcessStatus, String currentStatusLink, String resultLink) {
    this.documentProcessStatus = documentProcessStatus;
    this.currentStatusLink     = currentStatusLink;
    this.resultLink            = resultLink;
  }




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
