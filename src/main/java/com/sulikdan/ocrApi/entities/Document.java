package com.sulikdan.ocrApi.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Daniel Šulik on 02-Jul-20
 *
 * <p>Class Document is used for .....
 */
@Getter
@Setter
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
}
