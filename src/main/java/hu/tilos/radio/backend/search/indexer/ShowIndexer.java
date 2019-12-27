package hu.tilos.radio.backend.search.indexer;

import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class ShowIndexer extends BaseIndexer {

    @Override
    public Document indexRecord(DBObject show) {
        Document doc = new Document();
        doc.add(new TextField("name", safe(show, "name"), Field.Store.YES));
        doc.add(new TextField("alias", safe(show, "alias"), Field.Store.NO));
        doc.add(new TextField("description", safe(show, "description"), Field.Store.YES));
        doc.add(new TextField("introduction", safe(show, "description"), Field.Store.NO));
        doc.add(new TextField("type", "show", Field.Store.YES));
        doc.add(new TextField("uri", "/show/" + safe(show, "alias"), Field.Store.YES));
        return doc;
    }

    @Override
    public String getCollection() {
        return "show";
    }
}
