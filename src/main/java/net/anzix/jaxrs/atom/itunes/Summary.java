package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;



@XmlRootElement(name = "summary", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Summary
{
  private String summary;

  public Summary() {}

  public Summary(String summary) { this.summary = summary; }



  @XmlValue
  public String getSummary() { return this.summary; }



  public void setSummary(String summary) { this.summary = summary; }
}
