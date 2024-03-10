package com.sulikdan.ocrApi.controllers;

import com.sulikdan.ocrApi.errors.UnsupportedLanguageException;
import java.util.Date;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class SharedController is class containing shared logic for controllers.
 */
public class SharedControllerLogic {

    /**
     * Generates name prefix for uploaded files. consisting of OCR_Timestamp
     *
     * @return strings "OCR_" + "current_timestamp_now()"
     * @implNote It's temporary solution and for many threaded usage, there may chance of collision
     * and may need to be tweaked with adding thread number to it.
     */
    protected static String generateNamePrefix() {
        Date now = new Date();
        return "OCR_" + now.getTime();
    }

    /**
     * Simple check supported languages. There are also other languages, but 1st they need to be
     * added here and then make sure that correct tesseract dataset is in folder.
     *
     * @param language expecting string in lower-case
     */
    protected static String checkAndParseSupportedLanguages(String language) {
        language = language.toLowerCase();
        switch (language) {
            case "czech":
            case "cz":
                return "ces";
            case "eng":
            case "english":
                return "eng";
            case "svk":
            case "slovak":
                return "slk";
            default:
                throw new UnsupportedLanguageException(language);
        }
    }
}
