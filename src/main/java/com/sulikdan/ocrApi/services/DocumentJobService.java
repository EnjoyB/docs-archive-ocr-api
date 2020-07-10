package com.sulikdan.ocrApi.services;

import java.nio.file.Path;

/**
 * Created by Daniel Šulik on 09-Jul-20
 * <p>
 * Class OCRResultStorage is used for .....
 */
public interface DocumentJobService extends Runnable {

    void setJobParams(Path filePath, String lang, Boolean highQuality);

}
