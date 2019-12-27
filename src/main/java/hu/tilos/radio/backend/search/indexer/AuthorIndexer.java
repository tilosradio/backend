package hu.tilos.radio.backend.search.indexer;

import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class AuthorIndexer extends BaseIndexer {
    @Override
    public Document indexRecord(DBObject authorRecord) {

        Document doc = new Document();
        doc.add(new TextField("name", safe(authorRecord, "name"), Field.Store.YES));

        doc.add(new TextField("alias", safe(authorRecord, "alias"), Field.Store.NO));

        if (authorRecord.get("introduction") != null) {
            doc.add(new TextField("introduction", authorRecord.get("introduction").toString(), Field.Store.NO));
            doc.add(new TextField("description", shorten(authorRecord.get("introduction").toString(), 100), Field.Store.YES));
        } else {
            doc.add(new TextField("description", "", Field.Store.YES));
        }

        doc.add(new TextField("type", "author", Field.Store.YES));
        doc.add(new TextField("uri", "/author/" + safe(authorRecord, "alias"), Field.Store.YES));
        return doc;
    }

    @Override
    public String getCollection() {
        return "author";
    }
}
