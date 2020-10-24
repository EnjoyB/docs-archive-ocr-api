package com.sulikdan.ocrApi.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daniel Šulik on 24-Oct-20
 * <p>
 * Class CustomProperties is used to get relevant data for tesseract.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tesseract")
public class CustomTessProperties {

    /**
     * Represing path to test data/
     */
    private String path;

}
