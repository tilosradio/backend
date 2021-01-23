package hu.tilos.radio.backend;

import com.mongodb.DBRef;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class DbRefCodec implements Codec<DBRef> {

    @Override
    public DBRef decode(BsonReader reader, DecoderContext decoderContext) {
        return null;
    }

    @Override
    public void encode(BsonWriter writer, DBRef value, EncoderContext encoderContext) {

    }

    @Override
    public Class<DBRef> getEncoderClass() {
        return null;
    }
}
