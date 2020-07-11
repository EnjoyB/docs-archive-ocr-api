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

  public DocumentAsyncStatus() {}

  private DocumentProcessStatus documentProcessStatus;
  private String currentStatusLink;
  private String resultLink;

  public static DocumentAsyncStatus generateDocumentAsyncStatus(
      DocumentStorageService service, DocumentProcessStatus processStatus, Path savedPath) {
    return DocumentAsyncStatus.builder()
        .documentProcessStatus(processStatus)
        .currentStatusLink(service.getGetDocumentAsyncUri() + savedPath.getFileName().toString())
        .resultLink(service.getGetDocumentUri() + savedPath.getFileName().toString())
        .build();
  }
}
