package com.sulikdan.ocrApi.entities;

import java.util.List;

/**
 * Created by Daniel Šulik on 02-Jul-20
 *
 * <p>Class Document is used for .....
 */
public class Document {
  //    Multi
  private String name;
  private String origName;
  private String url;
  private List<String> pages;


  public Document(String name, String url, List<String> pages) {
    this.name = name;
    this.url = url;
    this.pages = pages;
  }

  public Document(String name, String origName, String url, List<String> pages) {
    this.name     = name;
    this.origName = origName;
    this.url      = url;
    this.pages    = pages;
  }

  public List<String> getPages() {
    return pages;
  }

  public void setPages(List<String> pages) {
    this.pages = pages;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
