package com.sulikdan.ocrApi.controllers;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import com.sulikdan.ocrApi.services.PDFService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class PDFControllerTest {

    @Mock
    TaskExecutor taskExecutor;
    @Mock
    DocumentStorageService documentStorageService;
    @Mock
    OCRService ocrService;
    @Mock
    FileStorageService fileStorageService;
    @Mock
    PDFService pdfService;
    ObjectMapper mapper = new ObjectMapper();
    @InjectMocks
    PDFController pdfController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pdfController).build();
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

        when(pdfService.processPDFs(any(), any(OcrConfig.class))).thenReturn(listDoc);

        // when
        MvcResult mvcResult =
            this.mockMvc
                .perform(
                    post("/pdfs")
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
        // given
        ConcurrentHashMap<String, Document> hashMap = new ConcurrentHashMap<>();
        Document d = new Document("foo", "yolo.com", new ArrayList<>());
        hashMap.put(d.getName(), d);

        ConcurrentHashMap<String, DocumentAsyncStatus> map = new ConcurrentHashMap<>();

        when(documentStorageService.getDocumentMap()).thenReturn(hashMap);
        when(documentStorageService.getDocumentMap()).thenReturn(hashMap);
        when(documentStorageService.getDocumentAsyncMap()).thenReturn(map);

        // when
        this.mockMvc.perform(delete("/pdfs/{fileName}", "foo")).andExpect(status().isNoContent());

        // then
        verify(documentStorageService, times(2)).getDocumentMap();
        verify(documentStorageService, times(1)).getDocumentAsyncMap();
    }

    @Test
    void getDocumentNull() throws Exception {
        // given
        ConcurrentHashMap<String, Document> hashMap = new ConcurrentHashMap<>();
        Document d = null;

        when(documentStorageService.getDocumentMap()).thenReturn(hashMap);

        // when
        MvcResult mvcResult =
            this.mockMvc
                .perform(get("/pdfs/{fileName}", "foo"))
                .andExpect(status().isNotFound())
                .andReturn();
        // then
        Assert.assertEquals("", mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getDocument() throws Exception {
        // given
        ConcurrentHashMap<String, Document> hashMap = new ConcurrentHashMap<>();
        Document d = new Document("foo", "yolo.com", new ArrayList<>());
        hashMap.put(d.getName(), d);

        when(documentStorageService.getDocumentMap()).thenReturn(hashMap);

        // when
        MvcResult mvcResult =
            this.mockMvc
                .perform(get("/pdfs/{fileName}", "foo"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        Assert.assertEquals(mapper.writeValueAsString(d),
            mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getDocumentStatusNotFound() throws Exception {
        // given
        ConcurrentHashMap<String, DocumentAsyncStatus> map = new ConcurrentHashMap<>();

        when(documentStorageService.getDocumentAsyncMap()).thenReturn(map);

        // when
        MvcResult mvcResult =
            this.mockMvc
                .perform(get("/pdfs/{fileName}/documentStatus", "foo"))
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
                .perform(get("/pdfs/{fileName}/documentStatus", "foo"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        Assert.assertEquals(
            mapper.writeValueAsString(documentAsyncStatus),
            mvcResult.getResponse().getContentAsString());
    }
}
