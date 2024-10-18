package com.sulikdan.ocrApi.services.async;

import com.sulikdan.ocrApi.configurations.properties.CustomServerProperties;
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

    protected final CustomServerProperties customServerProperties;

    // For async communication
    protected final ConcurrentHashMap<String, Document> documentSyncMap;
    protected final ConcurrentHashMap<String, DocumentAsyncStatus> documentAsyncMap;

    // Uris
    protected String baseUriDomain = "";
    protected String getDocumentUri = "";
    protected String getDocumentAsyncUri = "";

    public DocumentStorageServiceImpl(CustomServerProperties customServerProperties) {
        this.customServerProperties = customServerProperties;

        this.documentSyncMap = new ConcurrentHashMap<>();
        this.documentAsyncMap = new ConcurrentHashMap<>();
        System.out.println("Created HashMaps!");
        log.debug("Created HashMaps!");

        this.baseUriDomain =
                "http://" + customServerProperties.getAddress() + ":"
                        + customServerProperties.getPort();
        this.getDocumentUri =
                baseUriDomain + customServerProperties.getContextPath() + "/documents/";

        this.getDocumentAsyncUri =
                baseUriDomain + customServerProperties.getContextPath() + "/documentStatus/";
        log.info("Set new uris to :" + getDocumentAsyncUri);
    }

    @Override
    public ConcurrentHashMap<String, Document> getDocumentSyncMap() {
        return documentSyncMap;
    }


    @Override
    public Document getDocumentFromSyncMap(String key) {
        return documentSyncMap.get(key);
    }

    @Override
    public void putDocumentToSyncMap(String key, Document document) {
        documentSyncMap.put(key, document);
    }

    @Override
    public void removeDocumentFromSyncMap(String key) {
        documentSyncMap.remove(key);
    }

    @Override
    public boolean containsDocumentSync(String documentId) {
        return documentSyncMap.containsKey(documentId);
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


//    @Override
//    public Document putDocumentToAsyncMap(String key, Document document) {
//        return documentAsyncMap.put(key, document);
//    }
//
//    @Override
//    public boolean containsDocumentSync(String documentId) {
//        return documentAsyncMap.containsKey(documentId);
//    }

    @Override
    public String getGetDocumentAsyncUri() {
        return getDocumentAsyncUri;
    }

    @Override
    public void setGetDocumentAsyncUri(String getDocumentAsyncUri) {
        System.out.println("Setting getDocumentAsyncUri:\n" + getDocumentAsyncUri);
        this.getDocumentAsyncUri = getDocumentAsyncUri;
    }

    @Override
    public ConcurrentHashMap<String, DocumentAsyncStatus> getDocumentAsyncMap() {
        return documentAsyncMap;
    }

//    @Override
//    public ConcurrentHashMap<String, DocumentAsyncStatus> getDocumentAsyncMap() {
//        return documentAsyncMap;
//    }
//
//    @Override
//    public boolean containsDocumentASync(String documentId) {
//        return documentAsyncMap.containsKey(documentId);
//    }

}
