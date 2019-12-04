package hu.tilos.radio.backend.episode;

import org.junit.Assert;
import org.junit.Test;

public class ImageExtractorTest {

    @Test
    public void testExtractFirstImage() throws Exception {
        String text = "![Tamási Miklós](https://scontent-vie1-1.xx.fbcdn.net/hphotos-xft1/v/t1.0-0/s480x480/12832291_483547478496076_1837951719964161950_n.png?oh=5859fee6caa04d00d7696d3de130a0e7&oe=57662C9F)\n" +
                "\n" +
                "Fortepan fotóarchívumot 2010 őszén alapította Szepessy Ákossal. A 20. század Magyarországát bemutató online archívum, amelynek minden egyes képe nyomtatási minőségben, ingyenesen letölthető, ötezer lomtalanításokon és ócskapiacon gyűjtött, főleg családi albumokból származó fényképpel indult. Mára több mint 65 ezer képet tartalmaz a világon egyedülálló gyűjtemény.\n" +
                "\n" +
                "http://www.fortepan.hu/\n" +
                "http://www.ng.hu/Elofizetoi-tamogatas/2015/01/06/Kepes-mindenre";

        ImageExtractor extractor = new ImageExtractor();

        String image = extractor.extractFirstImage(text);

        Assert.assertEquals("https://scontent-vie1-1.xx.fbcdn.net/hphotos-xft1/v/t1.0-0/s480x480/12832291_483547478496076_1837951719964161950_n.png?oh=5859fee6caa04d00d7696d3de130a0e7&oe=57662C9F", image);
    }
}