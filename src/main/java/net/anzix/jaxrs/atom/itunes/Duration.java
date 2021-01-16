package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;



@XmlRootElement(name = "duration", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Duration
{
  private long seconds;

  public Duration() {}

  public Duration(long seconds) { this.seconds = seconds; }



  @XmlValue
  public long getSeconds() { return this.seconds; }



  public void setSeconds(long seconds) { this.seconds = seconds; }
}
