package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniel Šulik on 12-Jul-20
 *
 * <p>Class PDFServiceImpl is used for work with PDF files. Reads PDF file via pdfbox then saves it
 * as png file. In case of multiple-page pdf it will be saved as multiple png files.
 *
 * <p>Though pdfbox supports to read a pdf file as text(if the file supports it), but in this case,
 * it's converted to PNG file and then send to OCR
 */
@Service
public class PDFServiceImpl implements PDFService {

  private final FileStorageService fileStorageService;
  private final OCRService ocrService;

  public PDFServiceImpl(FileStorageService fileStorageService, OCRService ocrService) {
    this.fileStorageService = fileStorageService;
    this.ocrService = ocrService;
  }

  @Override
  public Document extractTextFromPDF(
      Path pdfFilePath,
      String origFileName,
      String lang,
      Boolean multipageTiff,
      Boolean highQuality) {

    try {
      PDDocument pdfDoc = PDDocument.load(pdfFilePath.toFile());
      PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);

      StringBuilder out = new StringBuilder();
      List<Document> pdfPages = new ArrayList<>();

      for (int pageNum = 0; pageNum < pdfDoc.getNumberOfPages(); pageNum++) {
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNum, 300, ImageType.RGB);

        // Create a temp image file from PDF page
        Path tempFilePath =
            fileStorageService.saveTmpFile(
                bufferedImage, pageNum, pdfFilePath.getFileName().toString());

        // OCR scanning
        pdfPages.add(
            ocrService.extractTextFromFile(
                tempFilePath, origFileName, lang, multipageTiff, highQuality));

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
              pdfFilePath.getFileName().toString(), origFileName, pdfPages.get(0).getUrl(), pages);

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
      PDDocument pdfDoc = PDDocument.load(pdfFilePath.toFile());
      PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);

      for (int pageNum = 0; pageNum < pdfDoc.getNumberOfPages(); pageNum++) {
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(pageNum, 300, ImageType.RGB);

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
}
