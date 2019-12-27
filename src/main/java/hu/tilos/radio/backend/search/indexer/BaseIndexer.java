package hu.tilos.radio.backend.search.indexer;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BaseIndexer {

    private static final Logger LOG = LoggerFactory.getLogger(BaseIndexer.class);

    public void index(DB db, IndexWriter w) {
        DBCursor result = db.getCollection(getCollection()).find();
        for (DBObject record : result) {
            try {
                String id = ((ObjectId) record.get("_id")).toString();
                Document doc = indexRecord(record);
                if (doc != null) {
                    doc.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED));
                    w.updateDocument(new Term("id", id), doc);
                }
            } catch (IOException ex) {
                LOG.error("Can't update " + getCollection() + " record", ex);
            }
        }
    }


    public abstract Document indexRecord(DBObject record);

    public abstract String getCollection();

    public String shorten(String text, int i) {
        if (text.length() > 100) {
            return text.substring(0, 99) + "...";
        } else {
            return text;
        }
    }

    public String safe(DBObject object, String content) {
        if (object.get(content) == null) {
            return "";
        }
        return object.get(content).toString();
    }
}
