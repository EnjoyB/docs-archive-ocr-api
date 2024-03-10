package com.sulikdan.ocrApi.services.wrappers;

import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

@Component
public class PDDocumentWrapper {

    public static final PDDocumentWrapper SINGLETON = new PDDocumentWrapper();

    public PDDocument loadPdfFile(File file) throws IOException {
        return PDDocument.load(file);
    }
}
