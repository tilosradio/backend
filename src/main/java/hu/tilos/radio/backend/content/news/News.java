package hu.tilos.radio.backend.content.news;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

public class News {

    @Id
    private String id;

    private String title;

    private String alias;

    private String content;

    @Transient
    private String formatted;

    @Transient
    private String leadFormatted;

    @Transient
    private boolean longText;

    private Date created = new Date();

    public String getLeadFormatted() {
        return leadFormatted;
    }

    public void setLeadFormatted(String leadFormatted) {
        this.leadFormatted = leadFormatted;
    }

    public boolean isLongText() {
        return longText;
    }

    public void setLongText(boolean longText) {
        this.longText = longText;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

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
