package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.async.DocumentJobWorker;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import com.sulikdan.ocrApi.services.async.DocumentStorageServiceImpl;
import jdk.jfr.ContentType;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceImplTest {

  @Mock DocumentStorageService documentStorageService;
  @Mock FileStorageService fileStorageService;
  @Mock OCRService ocrService;
  @Mock TaskExecutor taskExecutor;

  DocumentService documentService;

  Document document;
  DocumentAsyncStatus asyncStatus;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    documentStorageService = mock(DocumentStorageServiceImpl.class, RETURNS_DEEP_STUBS);
    fileStorageService = mock(FileStorageServiceImpl.class, RETURNS_DEEP_STUBS);

    document = new Document("yoloDoc", "www.yolo.com", new ArrayList<>());

    asyncStatus = new DocumentAsyncStatus();

    documentService =
        new DocumentServiceImpl(
            documentStorageService, fileStorageService, ocrService, taskExecutor);
  }

  @Test
  void deleteDocument() {
    final String fileName = "randomDoc";

    // Given
    when(documentStorageService.getDocumentMap().containsKey(anyString())).thenReturn(true);
    when(documentStorageService.getDocumentMap().remove(anyString())).thenReturn(this.document);

    when(documentStorageService.getDocumentAsyncMap().remove(anyString()))
        .thenReturn(this.asyncStatus);

    // When
    documentService.deleteDocument(fileName);

    // Then
    verify(documentStorageService.getDocumentMap(), times(1)).containsKey(anyString());
    verify(documentStorageService.getDocumentMap(), times(1)).remove(anyString());
    verify(documentStorageService.getDocumentAsyncMap(), times(1)).remove(anyString());
  }

  @Test
  void deleteDocumentNoneFound() {
    final String fileName = "randomDoc";

    // Given
    when(documentStorageService.getDocumentMap().containsKey(anyString())).thenReturn(false);

    // When
    documentService.deleteDocument(fileName);

    // Then
    verify(documentStorageService.getDocumentMap(), times(1)).containsKey(anyString());
    verify(documentStorageService.getDocumentMap(), times(0)).remove(anyString());
    verify(documentStorageService.getDocumentAsyncMap(), times(0)).remove(anyString());
  }

  @Test
  void getDocument() {
    // given
    final String fileName = "randomDoc";

    when(documentStorageService.getDocumentMap().get(anyString())).thenReturn(document);

    // when
    documentService.getDocument(fileName);

    // then
    verify(documentStorageService.getDocumentMap(), times(1)).get(anyString());
  }

  @Test
  void processDocuments() {
    MultipartFile[] files = new MultipartFile[1];
    MultipartFile result =
        new MockMultipartFile("name", "originalFileName", "text/plain", new byte[4]);
    files[0] = result;

    OcrConfig ocrConfig = new OcrConfig();

    Path path = Paths.get("/tmp/foo.jpg");

    // Given
    when(fileStorageService
            .saveFile(any(MultipartFile.class), anyString())
            .getFileName()
            .toString())
        .thenReturn("foo.jpg");

    taskExecutor.execute(any(DocumentJobWorker.class));
    //    when(files[0].getOriginalFilename()).thenReturn("foo.jpg");
    when(documentStorageService
            .getDocumentAsyncMap()
            .put(anyString(), any(DocumentAsyncStatus.class)))
        .thenReturn(new DocumentAsyncStatus());

    // When
    List<DocumentAsyncStatus> resultList = documentService.processDocuments(files, ocrConfig);

    // Then
    Assert.assertNotNull(resultList);
    Assert.assertEquals(1, resultList.size());

    verify(fileStorageService, times(1)).saveFile(any(MultipartFile.class), anyString());
    verify(taskExecutor, times(1)).execute(any(DocumentJobWorker.class));
    verify(documentStorageService.getDocumentAsyncMap(),times(1)).put(anyString(),any(DocumentAsyncStatus.class));
  }

  @Test
  void processDocumentsSync() {
    // Given
    MultipartFile[] files = new MultipartFile[1];
    MultipartFile result =
            new MockMultipartFile("name", "originalFileName", "text/plain", new byte[4]);
    files[0] = result;

    OcrConfig ocrConfig = new OcrConfig();

    when(fileStorageService.saveFile(any(MultipartFile.class), anyString())).thenReturn(Paths.get("foo.jpg"));
    fileStorageService.deleteFile(any(Path.class));

    // When
    List<Document> documentList = documentService.processDocumentsSync(files,ocrConfig);

    // Then
    Assert.assertNotNull(documentList);
    Assert.assertEquals(1, documentList.size());
    verify(fileStorageService,times(1)).saveFile(any(MultipartFile.class), anyString());
    verify(fileStorageService,times(1)).deleteFile(any(Path.class));

  }
}
