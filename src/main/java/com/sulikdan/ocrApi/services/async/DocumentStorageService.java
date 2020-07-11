package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Daniel Šulik on 10-Jul-20
 * <p>
 * Class DocumentStorageService is used for .....
 */
public interface DocumentStorageService {

    void setGetDocumentUri(String uri);
    String getGetDocumentUri();

    void setGetDocumentAsyncUri(String uri);
    String getGetDocumentAsyncUri();

    ConcurrentHashMap<String, Document> getDocumentMap();
    ConcurrentHashMap<String, DocumentAsyncStatus> getDocumentAsyncMap();


//    void addDocumentToDocumentMap(String fileName, Document document);
//
//
//    void addDocumentAsyncToDocumentAsyncMap(String fileName, DocumentAsync documentAsync);


}
