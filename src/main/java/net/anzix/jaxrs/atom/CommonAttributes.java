package net.anzix.jaxrs.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Attributes common across all atom types
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CommonAttributes {
    private URI base;

    private String lang;
    
    private Map extensionAttributes = new HashMap();

    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    public URI getBase() {
        return base;
    }

    public void setBase(URI base) {
        this.base = base;
    }


    @XmlAttribute(namespace = "http://www.w3.org/XML/1998/namespace")
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @XmlAnyAttribute
    public Map getExtensionAttributes() {
        return extensionAttributes;
    }
}
