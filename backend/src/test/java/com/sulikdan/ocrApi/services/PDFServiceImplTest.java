package com.sulikdan.ocrApi.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import com.sulikdan.ocrApi.services.async.PDFJobWorker;
import com.sulikdan.ocrApi.services.wrappers.PDDocumentWrapper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

// @PrepareForTest(PDDocument.class)
class PDFServiceImplTest {

    @Mock
    TaskExecutor taskExecutor;
    @Mock
    DocumentStorageService documentStorageService;
    @Mock
    FileStorageService fileStorageService;
    @Mock
    OCRService ocrService;

    @Mock
    PDDocumentWrapper pdDocumentWrapper;

    @Mock
    List<Path> covnertedPaths;

    @Mock
    List<DocumentAsyncStatus> documentAsyncStatuses;

    PDFService pdfService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        documentStorageService = mock(DocumentStorageService.class, RETURNS_DEEP_STUBS);
        fileStorageService = mock(FileStorageService.class, RETURNS_DEEP_STUBS);

        pdfService =
            new PDFServiceImpl(
                taskExecutor,
                documentStorageService,
                fileStorageService,
                ocrService,
                pdDocumentWrapper);
    }

    @Test
    void extractTextFromPDF() throws Exception {
        // Given
        final Path filePathPdf = Paths.get("foo.pdf");
        final Path filePathPng = Paths.get("foo.png");
        final Document doc = new Document("foo.png", "yolo.com", new ArrayList<>());
        doc.getPages().add("New page 1.");
        List<Document> pdfPages = new ArrayList<>();
        pdfPages.add(doc);

        PDDocument pdDocument = new PDDocument();

        final String origFileName = "foo.pdf";
        final OcrConfig ocrConfig = new OcrConfig();

        //        PDDocument pdDocument1 = new PDDocument();
        when(pdDocumentWrapper.loadPdfFile(any(File.class))).thenReturn(pdDocument);
        when(fileStorageService.saveTmpFile(any(BufferedImage.class), anyInt(), anyString()))
            .thenReturn(filePathPng);
        when(ocrService.extractTextFromFile(any(Path.class), anyString(), any(OcrConfig.class)))
            .thenReturn(doc);
        pdfPages.add(any(Document.class));
        fileStorageService.deleteFile(filePathPng);
        fileStorageService.deleteFile(filePathPdf);

        // When
        Document docExtracted = pdfService.extractTextFromPDF(filePathPdf, origFileName, ocrConfig);

        // Then
        assertNotNull(docExtracted);
    }

    @Test
    void convertPDFToPNG() throws IOException {
        // Given
        final String originalName = "foo.pdf";
        final Path filePathPdf = Paths.get(originalName);
        final Path filePathPng = Paths.get("foo.png");

        PDDocument pdDocument = new PDDocument();

        when(pdDocumentWrapper.loadPdfFile(any(File.class))).thenReturn(pdDocument);
        when(fileStorageService.saveTmpFile(any(BufferedImage.class), anyInt(), anyString()))
            .thenReturn(filePathPng);
        when(covnertedPaths.add(any(Path.class))).thenReturn(true);

        // When
        List<Path> pathList = pdfService.convertPDFToPNG(filePathPdf, originalName);

        // Then
        assertNotNull(pathList);
        verify(pdDocumentWrapper, times(1)).loadPdfFile(any(File.class));
    }

    @Test
    void processPDFs() {
        // Given
        MultipartFile[] files = new MultipartFile[1];
        final String fileName = "originalFileName.pdf";
        final Path filePath = Paths.get(fileName);
        MultipartFile result =
            new MockMultipartFile("name", "originalFileName.pdf", "text/plain", new byte[4]);
        files[0] = result;

        OcrConfig ocrConfig = new OcrConfig();
        when(fileStorageService
            .saveFile(any(MultipartFile.class), anyString())
            .getFileName()
            .toString())
            .thenReturn("foo.jpg");
        taskExecutor.execute(any(PDFJobWorker.class));
        documentAsyncStatuses.add(any(DocumentAsyncStatus.class));
        documentStorageService.getDocumentAsyncMap()
            .put(anyString(), any(DocumentAsyncStatus.class));

        // When
        List<DocumentAsyncStatus> statuses = pdfService.processPDFs(files, ocrConfig);

        // Then
        assertNotNull(statuses);
        assertNotEquals(0, statuses.size());

        verify(fileStorageService, times(1)).saveFile(any(MultipartFile.class), anyString());
        verify(taskExecutor, times(1)).execute(any(PDFJobWorker.class));
        verify(documentStorageService.getDocumentAsyncMap(), times(1)).put(anyString(),
            any(DocumentAsyncStatus.class));
    }
}
