package hu.tilos.radio.backend.contribution;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class ShowReference {

    @BsonProperty("ref")
    private String id;

    private String name;

    private String alias;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
