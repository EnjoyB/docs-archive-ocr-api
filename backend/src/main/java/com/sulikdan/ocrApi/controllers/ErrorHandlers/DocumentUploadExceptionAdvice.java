package com.sulikdan.ocrApi.controllers.ErrorHandlers;

// import net.sourceforge.tess4j.TesseractException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sulikdan.ocrApi.errors.UnsupportedLanguageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by Daniel Šulik on 09-Jul-20
 *
 * <p>Class DocumentUploadExceptionAdvice is to cover exception returned from lower layers and
 * return corresponding response.
 */
@ControllerAdvice
public class DocumentUploadExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleTesseractException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(JsonProcessingException.class.getName() + " exception:\n" + e.getMessage());
    }

    @ExceptionHandler(UnsupportedLanguageException.class)
    public ResponseEntity<String> handleUnsupportedLanguage(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(UnsupportedLanguageException.class.getName() + " exception:\n" + e.getMessage());
    }
}
