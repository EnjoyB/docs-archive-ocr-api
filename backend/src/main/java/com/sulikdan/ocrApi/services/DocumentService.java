package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 13-Aug-20
 * <p>
 * Class DocumentService is used for distribution of work & for processing received documents from
 * ImgDocumentController.
 */
public interface DocumentService {

    /**
     * @param fileName
     */
    void deleteDocument(String fileName);

    /**
     * @param fileName
     * @return
     */
    Document getDocument(String fileName);

    /**
     * @param files
     * @param scanConfig
     * @return
     */
    List<DocumentAsyncStatus> processDocuments(MultipartFile[] files, OcrConfig scanConfig);

    /**
     * @param files
     * @param ocrConfig
     * @return
     */
    List<Document> processDocumentsSync(MultipartFile[] files, OcrConfig ocrConfig);
}
