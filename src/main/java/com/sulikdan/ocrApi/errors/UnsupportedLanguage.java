package com.sulikdan.ocrApi.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Daniel Šulik on 11-Jul-20
 *
 * <p>Class UnsopprotedLanguage is used for .....
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedLanguage extends RuntimeException {
  public UnsupportedLanguage() {
    super();
  }

  public UnsupportedLanguage(String message) {
    super(message);
  }

  public UnsupportedLanguage(String message, Throwable cause) {
    super(message, cause);
  }
}
