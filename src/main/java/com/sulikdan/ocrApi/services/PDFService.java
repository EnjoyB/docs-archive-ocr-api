package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;

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
      String lang,
      Boolean multipageTiff,
      Boolean highQuality);

  List<Path> convertPDFToPNG(Path pdfFilePath,
                             String origFileName);
}
