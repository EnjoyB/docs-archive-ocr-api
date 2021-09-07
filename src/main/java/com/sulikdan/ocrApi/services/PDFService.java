package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import java.nio.file.Path;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class PDFService is used for work with PDF files. Reads PDF file via pdfbox then saves it
 * as png file. In case of multiple-page pdf it will be saved as multiple png files.
 *
 * <p>Though pdfbox supports to read a pdf file as text(if the file supports it), but in this case,
 * it's converted to PNG file and then send to OCR
 */
public interface PDFService {

    Document extractTextFromPDF(
        Path pdfFilePath,
        String origFileName,
        OcrConfig ocrConfig);

    /**
     * @param pdfFilePath
     * @param origFileName
     * @return
     */
    List<Path> convertPDFToPNG(Path pdfFilePath,
        String origFileName);

    /**
     * @param files
     * @param ocrConfig
     * @return
     */
    List<DocumentAsyncStatus> processPDFs(MultipartFile[] files, OcrConfig ocrConfig);
}
