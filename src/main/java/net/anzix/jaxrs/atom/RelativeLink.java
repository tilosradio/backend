package net.anzix.jaxrs.atom;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

/**
 * If invoked within the context of a JAX-RS call, it will automatically build a
 * URI based the base URI of the JAX-RS application.  Same URI as UriInfo.getRequestUri().
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class RelativeLink extends Link {
    public RelativeLink() {
    }

    public RelativeLink(String rel, URI relativeLink) {
        setHref(relativeLink);
        setRel(rel);
    }

    public RelativeLink(String rel, URI relativeLink, MediaType mediaType) {
        this(rel, relativeLink);
        this.setType(mediaType);
    }

}