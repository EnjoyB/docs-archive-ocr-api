package com.sulikdan.ocrApi.services.wrappers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class PDDocumentWrapper {
    public static final PDDocumentWrapper SINGLETON = new PDDocumentWrapper();

    public PDDocument loadPdfFile(File file) throws IOException {
        return PDDocument.load(file);
    }
}
