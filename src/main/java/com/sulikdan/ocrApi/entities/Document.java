package com.sulikdan.ocrApi.entities;

/**
 * Created by Daniel Šulik on 02-Jul-20
 * <p>
 * Class Document is used for .....
 */
public class Document {
    //    Multi
    private String name;
    private String url;

    public Document(String name, String url) {
        this.name = name;
        this.url  = url;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
