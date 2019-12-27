package hu.tilos.radio.backend.content.page;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

public class Page {

    @Id
    private String id;

    private String title;

    private String alias;

    private String content;

    @Transient
    private String formatted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }
}
