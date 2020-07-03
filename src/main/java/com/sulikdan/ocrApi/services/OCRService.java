package com.sulikdan.ocrApi.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Šulik on 03-Jul-20
 * <p>
 * Class OCRService is used for .....
 */
public interface OCRService {

    String saveAndExtractText(MultipartFile file, Boolean highQuality);
}
