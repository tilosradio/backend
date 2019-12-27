package hu.tilos.radio.backend.search.indexer;

import com.mongodb.DBObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EpisodeIndexer extends BaseIndexer {



    public Document indexRecord(DBObject record) {
        DBObject text = (DBObject) record.get("text");
        if (text != null) {
            Document doc = new Document();
            doc.add(new TextField("content", safe(text, "content"), Field.Store.NO));
            //doc.add(new TextField("alias", safe(page.getAlias()), Field.Store.NO));
            doc.add(new TextField("name", safe(text, "title"), Field.Store.YES));
            doc.add(new TextField("description", shorten(safe(text, "content"), 100), Field.Store.YES));
            doc.add(new TextField("content", safe(text, "content"), Field.Store.NO));
            doc.add(new TextField("type", "episode", Field.Store.YES));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date plannedFrom = (Date) record.get("plannedFrom");
            String alias = (String) ((DBObject) record.get("show")).get("alias");
            doc.add(new TextField("uri", "/episode/" + alias + "/" + dateFormat.format(new Date(plannedFrom.getTime())), Field.Store.YES));
            return doc;
        } else {
            return null;
        }
    }


    public String getCollection() {
        return "episode";
    }
}
