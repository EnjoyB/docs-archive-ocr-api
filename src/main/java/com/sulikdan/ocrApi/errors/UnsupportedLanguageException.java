package com.sulikdan.ocrApi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class UnsupportedLanguageException is an exception used in case of an unsupported language.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedLanguageException extends RuntimeException {
  public UnsupportedLanguageException() {
    super();
  }

  public UnsupportedLanguageException(String message) {
    super(message);
  }

  public UnsupportedLanguageException(String message, Throwable cause) {
    super(message, cause);
  }
}
