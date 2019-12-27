package hu.tilos.radio.backend.search.indexer;

import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class PageIndexer extends BaseIndexer {
    @Override
    public Document indexRecord(DBObject page) {
        Document doc = new Document();

        doc.add(new TextField("content", safe(page, "content"), Field.Store.NO));
        doc.add(new TextField("alias", safe(page, "alias"), Field.Store.YES));
        doc.add(new TextField("name", safe(page, "title"), Field.Store.YES));
        doc.add(new TextField("description", shorten(safe(page, "content"), 100), Field.Store.YES));
        doc.add(new TextField("content", safe(page, "content"), Field.Store.NO));
        doc.add(new TextField("type", "page", Field.Store.YES));
        if (safe(page, "alias").length() > 0) {
            doc.add(new TextField("uri", "/page/" + page.get("alias"), Field.Store.YES));
        } else {
            doc.add(new TextField("uri", "/page/" + page.get("_id"), Field.Store.YES));
        }
        return doc;
    }

    @Override
    public String getCollection() {
        return "page";
    }
}
