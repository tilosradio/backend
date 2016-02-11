package hu.tilos.radio.backend.data.types;

/**
 * Data transfer object for text data.
 */
public class TextDataSimple {

    private String title;

    private String type;


    public TextDataSimple() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
