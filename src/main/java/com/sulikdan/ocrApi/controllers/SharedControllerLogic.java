package com.sulikdan.ocrApi.controllers;

import com.sulikdan.ocrApi.errors.UnsupportedLanguage;

/**
 * Created by Daniel Šulik on 11-Jul-20
 * <p>
 * Class SharedController is used for .....
 */
public class SharedControllerLogic {

    /**
     * Simple check supported languages. There are also other languages, but 1st they need to be added
     * here and then make sure that correct tesseract dataset is in folder.
     *
     * @param language expecting string in lower-case
     *
     */
    public static void checkSupportedLanguages(String language) {
        switch (language) {
            case "eng":
            case "cz":
            case "svk":
                return ;
            default:
                throw new UnsupportedLanguage(language);
        }
    }

}
