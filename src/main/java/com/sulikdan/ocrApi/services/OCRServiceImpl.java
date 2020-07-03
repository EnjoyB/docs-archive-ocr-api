package com.sulikdan.ocrApi.services;

import org.bytedeco.tesseract.TessBaseAPI;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 03-Jul-20
 * <p>
 * Class OCRServiceImpl is used for .....
 */
@Service
public class OCRServiceImpl implements OCRService {

    FileStorageService fileStorageService;
    TessBaseAPI tessBaseAPI;

    public OCRServiceImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;

        tessBaseAPI = new TessBaseAPI();
        if( tessBaseAPI.Init(null,"eng") != 0 ){
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
    }

    @Override
    public void saveAndExtractText(MultipartFile file, Boolean highQuality) {
        String savedPath = fileStorageService.saveFile(file);


    }
}
