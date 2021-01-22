package hu.tilos.radio.backend.util;

import com.mongodb.DBRef;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class DbrefSerializer extends JsonSerializer<DBRef> {

    @Override
    public void serialize(DBRef dbRef, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(dbRef.getId().toString());
    }
}
