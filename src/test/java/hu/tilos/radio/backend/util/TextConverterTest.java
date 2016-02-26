package hu.tilos.radio.backend.util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextConverterTest {

    @Test
    public void testParseMarkdownNonTilos() throws Exception {
        TextConverter textConverter = new TextConverter();


        String s = textConverter.parseMarkdown("[valami](http://index.hu)");

        Assert.assertEquals("<p><a href=\"http://index.hu\">valami</a></p>", s);
    }

    @Test
    public void testParseMarkdown() throws Exception {
        TextConverter textConverter = new TextConverter();


        String s = textConverter.parseMarkdown("[valami](http://tilos.hu) http://qwe.hu");

        Assert.assertEquals("<p><a href=\"http://tilos.hu\" target=\"_self\">valami</a> <a href=\"http://qwe.hu\">http://qwe.hu</a></p>", s);
    }


    @Test
    public void testParseMarkdownSimpleLink() throws Exception {
        TextConverter textConverter = new TextConverter();


        String s = textConverter.parseMarkdown("http://tilos.hu/asd");

        Assert.assertEquals("<p><a href=\"http://tilos.hu/asd\" target=\"_self\">http://tilos.hu/asd</a></p>", s);
    }
}