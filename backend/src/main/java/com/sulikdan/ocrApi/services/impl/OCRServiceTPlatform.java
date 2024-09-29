package com.sulikdan.ocrApi.services.impl;

import com.sulikdan.ocrApi.configurations.properties.CustomTessProperties;
import com.sulikdan.ocrApi.entities.Document;
import com.sulikdan.ocrApi.entities.OcrConfig;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.sulikdan.ocrApi.services.FileStorageService;
import com.sulikdan.ocrApi.services.OCRService;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.PIXA;
import org.bytedeco.leptonica.global.leptonica;
import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;

/**
 * Created by Daniel Šulik on 03-Jul-20
 *
 * <p>Class OCRServiceImpl is an implementation of OCRService.
 *
 * @see com.sulikdan.ocrApi.services.OCRService
 */
@Slf4j
@Service
public class OCRServiceTPlatform implements OCRService {

    private final FileStorageService fileStorageService;
    private final CustomTessProperties customTessProperties;
    private HashMap<String, TessBaseAPI> byLanguageTPlatform;

    public OCRServiceTPlatform(
        FileStorageService fileStorageService, CustomTessProperties customTessProperties) {
        this.fileStorageService = fileStorageService;
        this.customTessProperties = customTessProperties;
        this.byLanguageTPlatform = new HashMap<>();
        addTesseractLanguage("eng");
    }

    @Override
    public Document extractTextFromFile(
        Path savedFilePath, String origFileName, OcrConfig ocrConfig) {

        if (savedFilePath.toString().length() <= 0) {
            throw new RuntimeException("File not here or WrongFile");
        }
        // TODO

        Document extractedFile =
            new Document(savedFilePath.getFileName().toString(), origFileName, "",
                new ArrayList<>());

        if (ocrConfig.getMultiPages()) {
            PIXA images = leptonica.pixaReadMultipageTiff(savedFilePath.toString());

            for (int i = 0; i < images.n(); i++) {

                String scannedPage =
                    extractTextFromPix(images.pix(i), savedFilePath.getFileName().toString(),
                        ocrConfig);
                extractedFile.getPages().add(scannedPage);
            }

            // free resources
            leptonica.pixaDestroy(images);

        } else {
            PIX image = leptonica.pixRead(savedFilePath.toString());
            String scannedPage =
                extractTextFromPix(image, savedFilePath.getFileName().toString(), ocrConfig);
            extractedFile.getPages().add(scannedPage);

            // free resources
            leptonica.pixDestroy(image);
        }

        return extractedFile;
    }

    private String extractTextFromPix(PIX image, String newFileName, OcrConfig ocrConfig) {

        if (!addTesseractLanguage(ocrConfig.getLang())) {
            // TODO return error - unsupported language!
            return null;
        }

        // this is not going to work out with multiple threads ...
        // TODO recreate new instance for every file?
        TessBaseAPI tessBaseAPI = byLanguageTPlatform.get(ocrConfig.getLang());
        //    TessBaseAPI tessBaseAPI = CreateNewInstanceOfTessBaseAPI(lang);
        //    Don't forget on every new instance to delete it!!!! TODO

        // Setting page to OCR
        tessBaseAPI.SetImage(image);

        // Get OCR result
        BytePointer outText = tessBaseAPI.GetUTF8Text();
        log.debug("Extracted text from file {} with size: {}", newFileName, outText.getString()
                .length());
        System.out.println("Text extracted of length:\n" + outText.getString().length());

        String outputString = outText.getString();

        // Destroy used object and release memory
        outText.deallocate();

        return outputString;
    }

    @Override
    public boolean addTesseractLanguage(String language) {
        if (byLanguageTPlatform.containsKey(language)) {
            return true;
        }

        TessBaseAPI newTessBaseAPI = new TessBaseAPI();
        log.info("Custom tess prop: " + customTessProperties.getPath());

        if (newTessBaseAPI.Init(customTessProperties.getPath(), language) != 0) {
            log.error("Could not initialize tesseract, with language: {}", language);
            return false;
        }
        byLanguageTPlatform.put(language, newTessBaseAPI);
        return true;
    }

    /**
     * Need to be tweaked with queue containing available instances? to avoid recreate them!!! TODO
     * !!
     *
     * @param language
     * @return
     */
    public TessBaseAPI CreateNewInstanceOfTessBaseAPI(String language) {

        TessBaseAPI newTessBaseAPI = new TessBaseAPI();

        log.info("Custom tess prop: " + customTessProperties.getPath());

        if (newTessBaseAPI.Init(customTessProperties.getPath(), language) != 0) {
            log.error("Could not initialize tesseract, with language: {}", language);
            throw new RuntimeException(
                "Couldn't init another TessBaseAPI instance with language: " + language);
        }

        return newTessBaseAPI;
    }
}
