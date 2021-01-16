package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "explicit", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Explicit
{
  private String type = "clean";





  @XmlValue
  public String getType() { return this.type; }



  public void setType(String type) { this.type = type; }
}
