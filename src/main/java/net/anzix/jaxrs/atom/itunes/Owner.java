package net.anzix.jaxrs.atom.itunes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name = "owner", namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Owner
{
  private String name;
  private String email;

  public Owner() {}

  public Owner(String name, String email) {
    this.name = name;
    this.email = email;
  }


  @XmlElement
  public String getName() { return this.name; }



  public void setName(String name) { this.name = name; }



  @XmlElement
  public String getEmail() { return this.email; }



  public void setEmail(String email) { this.email = email; }
}
