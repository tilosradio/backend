package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "author", namespace = "http://www.itunes"
    + ".com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Author {
  private String name;

  public Author() {
  }

  public Author(String name) {
    this.name = name;
  }

  @XmlValue
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
