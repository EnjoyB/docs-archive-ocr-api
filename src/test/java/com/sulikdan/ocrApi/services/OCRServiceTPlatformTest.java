package com.sulikdan.ocrApi.services;

import com.sulikdan.ocrApi.configurations.properties.CustomTessProperties;
import org.bytedeco.tesseract.TessBaseAPI;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OCRServiceTPlatformTest {

  @Mock FileStorageService fileStorageService;

  @Mock CustomTessProperties customTessProperties;

  OCRService ocrService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);

    ocrService = new OCRServiceTPlatform(fileStorageService, customTessProperties);
  }

  @Test
  void addTesseractLanguage() {
    // Given
    final String language = "eng";

    when(customTessProperties.getPath()).thenReturn("RandomPath");
    when(customTessProperties.getPath()).thenReturn("RandomPath");

    // When
    boolean retVal = ocrService.addTesseractLanguage(language);

    // Then
    Assert.assertTrue(retVal);
    verify(customTessProperties, times(2)).getPath();
  }

}
