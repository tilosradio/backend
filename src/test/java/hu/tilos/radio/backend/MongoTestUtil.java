package hu.tilos.radio.backend;

import com.github.fakemongo.junit.FongoRule;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MongoTestUtil {

  public static final Logger LOG = LoggerFactory.getLogger(MongoTestUtil.class);

  public static void dropCollection(DB db, String collection) {
    db.getCollection(collection).drop();
  }
  public static String loadTo(DB db, String collection,
      String resourceName, String... references) {

    String json = loadFrom(resourceName, references);
    DBObject parsed = (DBObject) JSON.parse(json);
    db.getCollection(collection).insert(parsed);
    String id = ((ObjectId) parsed.get("_id")).toHexString();
    LOG.info("Object {} is inserted to {}", id, collection);
    return id;
  }

  public static String loadFrom(String resourceName, String... references) {
    InputStream resourceAsStream =
        MongoTestUtil.class.getResourceAsStream("/testdata/" + resourceName);
    if (resourceAsStream == null) {
      throw new RuntimeException("Resource is missing: " + resourceName);
    }
    String json = new Scanner(resourceAsStream).useDelimiter("//Z").next();
    for (int i = 1; i < references.length + 1; i++) {
      json = json.replaceAll("<REF" + i + ">", references[i - 1]);

    }
    return json;
  }

}
