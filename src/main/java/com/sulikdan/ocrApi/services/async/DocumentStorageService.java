package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daniel Šulik on 10-Jul-20
 * <p>
 * Class DocumentStorageService is virtual storage to keep information about currently processed
 * documents.
 */
public interface DocumentStorageService {

    String getGetDocumentUri();

    void setGetDocumentUri(String uri);

    String getGetDocumentAsyncUri();

    void setGetDocumentAsyncUri(String uri);

    ConcurrentHashMap<String, Document> getDocumentMap();

    ConcurrentHashMap<String, DocumentAsyncStatus> getDocumentAsyncMap();

}
