package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daniel Šulik on 10-Jul-20
 *
 * <p>Class DocumentStorageServiceImpl is used for .....
 */
@Slf4j
@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

  // For async communication
  protected final ConcurrentHashMap<String, Document> documentMap;
  protected final ConcurrentHashMap<String, DocumentAsyncStatus> documentAsyncMap;

  // Uris
  protected String getDocumentUri = "";
  protected String getDocumentAsyncUri = "";

  public DocumentStorageServiceImpl() {
    this.documentMap = new ConcurrentHashMap<>();
    this.documentAsyncMap = new ConcurrentHashMap<>();
    System.out.println("Created HashMaps!");
    log.debug("Created HashMaps!");

    this.getDocumentUri = "http://localhost:8080/ocr/async/document/";

    this.getDocumentAsyncUri ="http://localhost:8080/ocr/async/documentStatus/";
  }

  @Override
  public ConcurrentHashMap<String, Document> getDocumentMap() {
    return documentMap;
  }

  @Override
  public ConcurrentHashMap<String, DocumentAsyncStatus> getDocumentAsyncMap() {
    return documentAsyncMap;
  }

  @Override
  public String getGetDocumentUri() {
    return getDocumentUri;
  }

  @Override
  public void setGetDocumentUri(String getDocumentUri) {
    System.out.println("Setting getDocumentUri:\n" + getDocumentUri);
    this.getDocumentUri = getDocumentUri;
  }

  @Override
  public String getGetDocumentAsyncUri() {
    return getDocumentAsyncUri;
  }

  @Override
  public void setGetDocumentAsyncUri(String getDocumentAsyncUri) {
    System.out.println("Setting getDocumentAsyncUri:\n" + getDocumentAsyncUri);
    this.getDocumentAsyncUri = getDocumentAsyncUri;
  }
}
