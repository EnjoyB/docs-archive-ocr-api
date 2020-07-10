package com.sulikdan.ocrApi.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Daniel Šulik on 08-Jul-20
 * <p>
 * Class DocumentStatus is used for .....
 */
@Getter
@Setter
@Builder
public class DocumentAsync {
    private DocumentStatus documentStatus;
    private String currentStatusLink;
    private String resultLink;
}
