package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsync;

import java.util.HashMap;

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

    HashMap<String, Document> getDocumentMap();
    HashMap<String, DocumentAsync> getDocumentAsyncMap();


//    void addDocumentToDocumentMap(String fileName, Document document);
//
//
//    void addDocumentAsyncToDocumentAsyncMap(String fileName, DocumentAsync documentAsync);


}
