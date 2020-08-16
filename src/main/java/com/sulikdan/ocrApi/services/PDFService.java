package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.DocumentAsyncStatus;
import com.sulikdan.ocrApi.entities.OcrConfig;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class PDFService is used for .....
 */
public interface PDFService {

  Document extractTextFromPDF(
          Path pdfFilePath,
          String origFileName,
          OcrConfig ocrConfig);

  List<Path> convertPDFToPNG(Path pdfFilePath,
                             String origFileName);

  List<DocumentAsyncStatus> processPDFs(MultipartFile[] files, OcrConfig ocrConfig);
}
