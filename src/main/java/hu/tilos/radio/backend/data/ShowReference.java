package hu.tilos.radio.backend.data;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class ShowReference {

    private DBRef id;

    private String name;

    private String alias;

    public DBRef getId() {
        return id;
    }

    public void setId(DBRef id) {
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
