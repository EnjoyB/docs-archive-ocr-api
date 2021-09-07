package com.sulikdan.ocrApi.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sulikdan.ocrApi.configurations.properties.CustomTessProperties;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class OCRServiceTPlatformTest {

    @Mock
    FileStorageService fileStorageService;

    @Mock
    CustomTessProperties customTessProperties;

    OCRService ocrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ocrService = new OCRServiceTPlatform(fileStorageService, customTessProperties);
    }

    @Disabled
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
