package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.entities.Document;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;

//TODO This service is not used/implemented/developed any longer!!!!
@Profile("backUp")
@Service
public class OCRServiceTess4j extends OCRServiceShared implements OCRService {

    private FileStorageService fileStorageService;
    private HashMap<String , ITesseract> byLanguageITesseract;
    // TODO it's physical address! Need to consider include it to project ...

    /**
     * English language for tesseract is taken as defautl!
     * @param fileStorageService
     */
    public OCRServiceTess4j(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.byLanguageITesseract = new HashMap<>();
        addTesseractLanguage("eng");
    }

    @Override
    public Document saveAndExtractText(MultipartFile file, String lang, Boolean highQuality) throws TesseractException {
        String savedPath = fileStorageService.saveFile(file);

        StringBuilder stringBuilder = new StringBuilder();
        File tempReadFile = new File(savedPath);
//        File temp = File.createTempFile("tempfile_" + page, ".png");
//        ImageIO.write(bim, "png", temp);

        String result = byLanguageITesseract.get("eng").doOCR(tempReadFile);
        stringBuilder.append(result);

        // Delete temp file
//        .delete();
//        return stringBuilder.toString();
        return new Document("","", result);
    }

    @Override
    public boolean addTesseractLanguage(String language) {
        ITesseract newLanguageITesseract = new Tesseract();
        newLanguageITesseract.setDatapath(PATH_TO_TESSDATA);
        newLanguageITesseract.setLanguage(language);
        byLanguageITesseract.put(language,newLanguageITesseract);

        return true;
    }
}
