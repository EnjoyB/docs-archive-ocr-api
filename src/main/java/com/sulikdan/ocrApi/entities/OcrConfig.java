package com.sulikdan.ocrApi.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Daniel Šulik on 16-Aug-20
 * <p>
 * Class ScanConfig is an entity containing configuration for ocr-scanning.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrConfig {

    private String lang;
    private Boolean multiPages;
    private Boolean highQuality;


}
