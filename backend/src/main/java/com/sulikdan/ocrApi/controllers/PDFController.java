package com.sulikdan.ocrApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import com.sulikdan.ocrApi.services.PDFService;
import com.sulikdan.ocrApi.services.async.DocumentStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class PDFController is used to process PDF files and scan them with the help of a OCR.
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("pdfs")
public class PDFController extends SharedControllerLogic {

    private final ImgDocumentController imgDocumentController;
    private final DocumentStorageService documentStorageService;
    private final PDFService pdfService;
    private final ObjectMapper mapper = new ObjectMapper();

    public PDFController(
            ImgDocumentController imgDocumentController,
            DocumentStorageService documentStorageService,
            PDFService pdfService) {
        this.imgDocumentController = imgDocumentController;
        this.documentStorageService = documentStorageService;
        this.pdfService = pdfService;
    }

    /**
     * Used for uploading pdf file/s that will be solved.
     *
     * @param files
     * @param lang          language
     * @param multiPageFile
     * @param highQuality
     * @return Link with statuses of file/s.
     * @throws JsonProcessingException
     */
    @Operation(summary = "Used for uploading pdf file/s that will be solved.")
    @ResponseBody
    @PostMapping(consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadAndExtractTextAsync(
            @RequestPart("files") MultipartFile[] files,
            @RequestParam(value = "lang", defaultValue = "eng") String lang,
            @RequestParam(value = "multiPageFile", defaultValue = "false") Boolean multiPageFile,
            @RequestParam(value = "highQuality", defaultValue = "false") Boolean highQuality)
            throws JsonProcessingException {

        lang = checkAndParseSupportedLanguages(lang);
        OcrConfig ocrConfig =
                OcrConfig.builder().lang(lang).multiPages(multiPageFile).highQuality(highQuality)
                        .build();

        List<DocumentAsyncStatus> documentAsyncStatusList = pdfService.processPDFs(files,
                ocrConfig);

        log.info("Finishing in PDF async controller!");
        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.writeValueAsString(documentAsyncStatusList));
    }

    /**
     * Deletes document defined by file-name.
     *
     * @param fileName
     */
    @Operation(summary = "Deletes document defined by file-name.")
    @DeleteMapping("/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDocument(@PathVariable("fileName") String fileName) {
        // TODO reuse ImgDocumentController?
        if (documentStorageService.containsDocumentSync(fileName)) {
            documentStorageService.removeDocumentFromSyncMap(fileName);
            documentStorageService.getDocumentAsyncMap().remove(fileName);
        }
    }

    /**
     * Returns scanned document.
     *
     * @param fileName
     * @return
     * @throws JsonProcessingException
     */
    @Operation(summary = "Returns scanned document.")
    @GetMapping("/{fileName}")
    public ResponseEntity<?> getDocument(@PathVariable("fileName") String fileName)
            throws JsonProcessingException {
        return imgDocumentController.getDocument(fileName);
    }

    /**
     * Returns a document-status of the document's file processing.
     *
     * @param fileName
     * @return
     * @throws JsonProcessingException
     */
    @Operation(summary = "Returns a document-status of the document's file processing.")
    @GetMapping("/{fileName}/documentStatus")
    public ResponseEntity<?> getDocumentStatus(@PathVariable("fileName") String fileName)
            throws JsonProcessingException {
        // TODO reuse ImgDocumentController?
        DocumentAsyncStatus documentAsyncStatus =
                documentStorageService.getDocumentAsyncMap().get(fileName);

        if (documentAsyncStatus != null) {
            return ResponseEntity.ok(mapper.writeValueAsString(documentAsyncStatus));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
