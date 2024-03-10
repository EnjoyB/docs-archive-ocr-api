package com.sulikdan.ocrApi.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daniel Šulik on 24-Oct-20
 * <p>
 * Created for accessing application yml server properties.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "server")
public class CustomServerProperties {

    private String address;
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

}
