package net.anzix.jaxrs.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.anzix.jaxrs.atom.itunes.Author;
import net.anzix.jaxrs.atom.itunes.Category;
import net.anzix.jaxrs.atom.itunes.Explicit;
import net.anzix.jaxrs.atom.itunes.Image;
import net.anzix.jaxrs.atom.itunes.Owner;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"title", "subtitle", "categories", "updated", "id",
    "links", "authors", "contributors", "rights", "icon", "logo", "generator",
    "image", "anyOther"})
@XmlSeeAlso({Owner.class, Author.class, Owner.class, Explicit.class,
    Category.class})
public class Source
    extends CommonAttributes {
  private List<Person> authors = new ArrayList<Person>();

  private List<Category> categories = new ArrayList<Category>();

  private List<Person> contributors = new ArrayList<Person>();

  private Generator generator;

  private URI id;

  private String title;

  private Date updated;

  private List<Link> links = new ArrayList<Link>();

  private URI icon;

  private URI logo;

  private String rights;

  private String subtitle;

  private Image image;

  private Category category;

  private List<Object> anyOther;

  @XmlMixed
  @XmlAnyElement(lax = true)
  public List<Object> getAnyOther() {
    if (this.anyOther == null) {
      this.anyOther = new ArrayList();
    }
    return this.anyOther;
  }

  public void addAnyOther(Object any) {
    if (this.anyOther == null) {
      this.anyOther = new ArrayList();
    }
    this.anyOther.add(any);
  }

  @XmlElement(namespace = "http://www.itunes.com/dtds/podcast-1.0.dtd")
  public Image getImage() {
    return this.image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  @XmlElement(name = "author")
  public List<Person> getAuthors() {
    return this.authors;
  }

  @XmlElement(name = "contributor")
  public List<Person> getContributors() {
    return this.contributors;
  }

  @XmlElement
  public URI getId() {
    return this.id;
  }

  public void setId(URI id) {
    this.id = id;
  }

  @XmlElement
  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @XmlElement
  public Date getUpdated() {
    return this.updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  public Link getLinkByRel(String name) {
    for (Link link : this.links) {
      if (link.getRel().equals(name)) {
        return link;
      }
    }
    return null;
  }

  @XmlElementRef
  public List<Link> getLinks() {
    return this.links;
  }

  @XmlElementRef
  public List<Category> getCategories() {
    return this.categories;
  }

  @XmlElementRef
  public Generator getGenerator() {
    return this.generator;
  }

  public void setGenerator(Generator generator) {
    this.generator = generator;
  }

  @XmlElement
  public URI getIcon() {
    return this.icon;
  }

  public void setIcon(URI icon) {
    this.icon = icon;
  }

  @XmlElement
  public URI getLogo() {
    return this.logo;
  }

  public void setLogo(URI logo) {
    this.logo = logo;
  }

  @XmlElement
  public String getRights() {
    return this.rights;
  }

  public void setRights(String rights) {
    this.rights = rights;
  }

  @XmlElement
  public String getSubtitle() {
    return this.subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }
}
