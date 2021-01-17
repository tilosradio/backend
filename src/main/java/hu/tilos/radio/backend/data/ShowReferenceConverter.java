package hu.tilos.radio.backend.data;

import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

public class ShowReferenceConverter implements Converter<DBObject, ShowReference> {
    @Override
    public ShowReference convert(DBObject dbObject) {
        ShowReference showReference = new ShowReference();
        showReference.setAlias((String) dbObject.get("alias"));
        showReference.setName((String) dbObject.get("name"));
        DBRef ref = (DBRef) dbObject.get("ref");
        ObjectId refId = (ObjectId) ref.getId();
        showReference.setId(refId.toHexString());
        return showReference;
    }
}
