package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import com.sulikdan.ocrApi.services.async.DocumentStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceImplTest {

  @Mock DocumentStorageService documentStorageService;
  @Mock FileStorageService storageService;
  @Mock OCRService ocrService;
  @Mock TaskExecutor taskExecutor;

  DocumentService documentService;

  Document document;
  DocumentAsyncStatus asyncStatus;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    documentStorageService = mock(DocumentStorageServiceImpl.class, RETURNS_DEEP_STUBS);

    document = new Document("yoloDoc","www.yolo.com", new ArrayList<>());

    asyncStatus = new DocumentAsyncStatus();

    documentService =
        new DocumentServiceImpl(documentStorageService, storageService, ocrService, taskExecutor);
  }

  @Test
  void deleteDocument() {
    final String fileName = "randomDoc";

    // Given
    when(documentStorageService.getDocumentMap().containsKey(anyString())).thenReturn(true);
    when(documentStorageService.getDocumentMap().remove(anyString())).thenReturn(this.document);

    when(documentStorageService.getDocumentAsyncMap().remove(anyString())).thenReturn(this.asyncStatus);

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
    //given
    final String fileName = "randomDoc";

    when(documentStorageService.getDocumentMap().get(anyString())).thenReturn(document);

    //when
    documentService.getDocument(fileName);

    //then
    verify(documentStorageService.getDocumentMap(),times(1)).get(anyString());

  }

  @Test
  void processDocuments() {}

  @Test
  void processDocumentsSync() {}

  @Test
  void generateNamePrefix() {}
}
