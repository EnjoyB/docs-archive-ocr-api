package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Daniel Šulik on 13-Aug-20
 * <p>
 * Class DocumentService is used for .....
 */
public interface DocumentService {
    void deleteDocument(String fileName);

    Document getDocument(String fileName);

    List<DocumentAsyncStatus> processDocuments(MultipartFile[] files, OcrConfig scanConfig);

    List<Document> processDocumentsSync(MultipartFile[] files, OcrConfig ocrConfig);
}
