package hu.tilos.radio.backend;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import net.anzix.jaxrs.atom.*;
import net.anzix.jaxrs.atom.Summary;
import net.anzix.jaxrs.atom.itunes.*;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.net.URI;
import java.util.Date;
import java.util.HashSet;

public class FeedTransformer {


    public String render(Object model) throws Exception {
        Marshaller marshaller = createMarshaller();
        Feed feed = (Feed) model;
        HashSet<Class> set = new HashSet<Class>();
        set.add(Feed.class);
        for (Entry entry : feed.getEntries()) {
            if (entry.getAnyOtherJAXBObject() != null) {
                set.add(entry.getAnyOtherJAXBObject().getClass());
            }
            if (entry.getContent() != null && entry.getContent().getJAXBObject() != null) {
                set.add(entry.getContent().getJAXBObject().getClass());
            }
        }
        StringWriter w = new StringWriter();
        marshaller.marshal(feed, w);
        return w.toString();
    }

    private Marshaller createMarshaller() throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(Feed.class);


        Marshaller marshaller = ctx.createMarshaller();

        NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
            public String getPreferredPrefix(String namespace, String s1, boolean b) {
                if (namespace.equals("http://www.w3.org/2005/Atom")) return "atom";
                else return s1;
            }
        };

        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
        marshaller.setProperty("com.sun.xml.bind.indentString", "   ");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;

    }
}
