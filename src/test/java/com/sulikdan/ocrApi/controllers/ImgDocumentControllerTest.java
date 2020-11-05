package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.DocumentService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO -delete
// https://medium.com/backend-habit/integrate-junit-and-mockito-unit-testing-for-controller-layer-91bb4099c2a5
@ExtendWith(MockitoExtension.class)
class ImgDocumentControllerTest {

  private MockMvc mockMvc;

  @Mock DocumentStorageService documentStorageService;
  @Mock DocumentService documentService;

  ObjectMapper mapper = new ObjectMapper();

  @InjectMocks ImgDocumentController imgDocumentController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(imgDocumentController).build();
  }

  @Test
  void uploadAndExtractTextAsync() throws Exception {
    // given
    MultipartFile[] files = new MultipartFile[1];
    final String fileName = "originalFileName.jpg";
    MultipartFile result =
        new MockMultipartFile("name", "originalFileName.jpg", "text/plain", new byte[4]);
    files[0] = result;
    DocumentAsyncStatus d =
        new DocumentAsyncStatus(DocumentProcessStatus.PROCESSING, "yolo.com", "yolo2.com");
    List<DocumentAsyncStatus> listDoc = Collections.singletonList(d);

    when(documentService.processDocuments(any(), any(OcrConfig.class))).thenReturn(listDoc);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                post("/documents")
                    .requestAttr("files", files)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andReturn();

    // then
    Assert.assertEquals(
        mapper.writeValueAsString(listDoc), mvcResult.getResponse().getContentAsString());
  }

  @Test
  void deleteDocument() throws Exception {
    // when
    this.mockMvc.perform(delete("/documents/{fileName}", "foo")).andExpect(status().isNoContent());

    // then
    verifyNoInteractions(documentStorageService);
    verify(documentService, times(1)).deleteDocument(anyString());
  }

  @Test
  void getDocumentNull() throws Exception {
    // given
    Document d = null;
    when(documentService.getDocument(anyString())).thenReturn(d);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(get("/documents/{fileName}", "foo"))
            .andExpect(status().isNotFound())
            .andReturn();
    // then
    Assert.assertEquals("", mvcResult.getResponse().getContentAsString());
  }

  @Test
  void getDocument() throws Exception {
    // given
    Document d = new Document("foo", "yolo.com", new ArrayList<>());
    when(documentService.getDocument(anyString())).thenReturn(d);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(get("/documents/{fileName}", "foo"))
            .andExpect(status().isOk())
            .andReturn();
    // then
    Assert.assertEquals(mapper.writeValueAsString(d), mvcResult.getResponse().getContentAsString());
  }

  @Test
  void getDocumentStatusNotFound() throws Exception {
    // given
    ConcurrentHashMap<String, DocumentAsyncStatus> map = new ConcurrentHashMap<>();

    when(documentStorageService.getDocumentAsyncMap()).thenReturn(map);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(get("/documents/{fileName}/documentStatus", "foo"))
            .andExpect(status().isNotFound())
            .andReturn();
    // then
    Assert.assertEquals("", mvcResult.getResponse().getContentAsString());
  }

  @Test
  void getDocumentStatus() throws Exception {
    // given
    ConcurrentHashMap<String, DocumentAsyncStatus> map = new ConcurrentHashMap<>();
    DocumentAsyncStatus documentAsyncStatus =
        new DocumentAsyncStatus(DocumentProcessStatus.PROCESSING, "yolo.com", "yolo2.com");
    map.put("foo", documentAsyncStatus);

    when(documentStorageService.getDocumentAsyncMap()).thenReturn(map);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(get("/documents/{fileName}/documentStatus", "foo"))
            .andExpect(status().isOk())
            .andReturn();
    // then
    Assert.assertEquals(
        mapper.writeValueAsString(documentAsyncStatus),
        mvcResult.getResponse().getContentAsString());
  }

  @Test
  void uploadAndExtractTextSync() throws Exception {
    // given
    MultipartFile[] files = new MultipartFile[1];
    final String fileName = "originalFileName.pdf";
    MultipartFile result =
        new MockMultipartFile("name", "originalFileName.pdf", "text/plain", new byte[4]);
    files[0] = result;
    Document d = new Document("foo", "yolo.com", new ArrayList<>());
    List<Document> listDoc = Collections.singletonList(d);

    when(documentService.processDocumentsSync(any(), any(OcrConfig.class))).thenReturn(listDoc);

    // when
    MvcResult mvcResult =
        this.mockMvc
            .perform(
                post("/documents/sync")
                    .requestAttr("files", files)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isOk())
            .andReturn();

    // then
    Assert.assertEquals(
        mapper.writeValueAsString(listDoc), mvcResult.getResponse().getContentAsString());
  }
}
