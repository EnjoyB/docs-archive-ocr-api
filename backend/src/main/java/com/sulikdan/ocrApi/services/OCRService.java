package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.OcrConfig;
import java.nio.file.Path;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRService is used for .....
 * Inspired from https://medium.com/gft-engineering/creating-an-ocr-microservice-using-tesseract-pdfbox-and-docker-155beb7f2623
 */
public interface OCRService {

    static final String PATH_TO_TESSDATA_DEV = "src/main/resources/tessdata";
    static final String PATH_TO_TESSDATA = PATH_TO_TESSDATA_DEV;


    /**
     * @param savedFilePath
     * @param origFileName
     * @param ocrConfig
     * @return
     */
    Document extractTextFromFile(
        Path savedFilePath,
        String origFileName,
        OcrConfig ocrConfig);

    /**
     * @param language
     * @return
     */
    boolean addTesseractLanguage(String language);

}
