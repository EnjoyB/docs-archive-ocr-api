package com.sulikdan.ocrApi.services;

import static com.sulikdan.ocrApi.services.DocumentServiceImpl.generateNamePrefix;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.DocumentProcessStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import com.sulikdan.ocrApi.services.async.PDFJobWorker;
import com.sulikdan.ocrApi.services.wrappers.PDDocumentWrapper;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 12-Jul-20
 *
 * <p>Class PDFServiceImpl is an implementation of PDFService.
 *
 * @see com.sulikdan.ocrApi.services.PDFService
 */
@Slf4j
@AllArgsConstructor
@Service
public class PDFServiceImpl implements PDFService {

    private final TaskExecutor taskExecutor;

    private final DocumentStorageService documentStorageService;
    private final FileStorageService fileStorageService;
    private final OCRService ocrService;

    private final PDDocumentWrapper pdDocumentWrapper;


    @Override
    public Document extractTextFromPDF(Path pdfFilePath, String origFileName, OcrConfig ocrConfig) {

        try {
            PDDocument pdfDoc = pdDocumentWrapper.loadPdfFile(pdfFilePath.toFile());
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);

            StringBuilder out = new StringBuilder();
            List<Document> pdfPages = new ArrayList<>();

            for (int pageNum = 0; pageNum < pdfDoc.getNumberOfPages(); pageNum++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNum, 300,
                    ImageType.RGB);

                // Create a temp image file from PDF page
                Path tempFilePath =
                    fileStorageService.saveTmpFile(
                        bufferedImage, pageNum, pdfFilePath.getFileName().toString());

                // OCR scanning
                pdfPages.add(ocrService.extractTextFromFile(tempFilePath, origFileName, ocrConfig));

                // Delete temp file
                fileStorageService.deleteFile(tempFilePath);
            }

            // Merging One-page documents to one big multi-page document
            List<String> pages =
                pdfPages.stream()
                    .map(Document::getPages)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            Document finalProcessedDoc =
                new Document(
                    pdfFilePath.getFileName().toString(), origFileName,
                    pdfPages.size() > 0 ? pdfPages.get(0).getUrl()
                        : "Failed at extraction...very likely", pages);

            // Deleting saved pdfFile
            fileStorageService.deleteFile(pdfFilePath);

            return finalProcessedDoc;

        } catch (IOException e) {
            e.printStackTrace();
            // TODO IOException PDF file coulndt be read
            throw new RuntimeException(
                "Pdf file couldn't be read: " + pdfFilePath.getFileName().toString());
        }
    }

    @Override
    public List<Path> convertPDFToPNG(Path pdfFilePath, String origFileName) {
        List<Path> convertedPDFPaths = new ArrayList<>();

        try {
            PDDocument pdfDoc = pdDocumentWrapper.loadPdfFile(pdfFilePath.toFile());
            PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);

            for (int pageNum = 0; pageNum < pdfDoc.getNumberOfPages(); pageNum++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNum, 300,
                    ImageType.RGB);

                // Create a temp image file from PDF page
                convertedPDFPaths.add(
                    fileStorageService.saveTmpFile(
                        bufferedImage, pageNum, pdfFilePath.getFileName().toString()));
            }

            pdfDoc.close();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO IOException PDF file coulndt be read
            throw new RuntimeException(
                "Pdf file couldn't be read: " + pdfFilePath.getFileName().toString());
        }

        return convertedPDFPaths;
    }

    @Override
    public List<DocumentAsyncStatus> processPDFs(MultipartFile[] files, OcrConfig ocrConfig) {
        List<DocumentAsyncStatus> docsStatutes = new ArrayList<>();

        for (MultipartFile file : files) {
            Path savedFilePath = fileStorageService.saveFile(file, generateNamePrefix());

            String savedFileName = savedFilePath.getFileName().toString();

            log.info("Async sending work to do!");
            DocumentAsyncStatus returnAsyncStatus =
                DocumentAsyncStatus.generateDocumentAsyncStatus(
                    documentStorageService, DocumentProcessStatus.PROCESSING, savedFileName);

            taskExecutor.execute(
                new PDFJobWorker(
                    fileStorageService,
                    ocrService,
                    documentStorageService,
                    this,
                    savedFilePath,
                    file.getOriginalFilename(),
                    ocrConfig));

            docsStatutes.add(returnAsyncStatus);

            documentStorageService.getDocumentAsyncMap().put(savedFileName, returnAsyncStatus);
        }

        return docsStatutes;
    }
}
