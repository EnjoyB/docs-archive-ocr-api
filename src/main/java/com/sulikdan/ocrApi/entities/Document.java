package com.sulikdan.ocrApi.entities;

/**
 * Created by Daniel Šulik on 02-Jul-20
 *
 * <p>Class Document is used for .....
 */
public class Document {
  //    Multi
  private String name;
  private String url;
  private String text;

  public Document(String name, String url, String text) {
    this.name = name;
    this.url = url;
    this.text = text;
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
