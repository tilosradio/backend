package hu.tilos.radio.backend;

import hu.tilos.radio.backend.m3u.M3uService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

@RestController
public class M3uController {

    @Inject
    M3uService m3uService;

    @RequestMapping(value = "/api/v1/m3u/lastweek")
    public String weekly(@RequestParam(defaultValue = "/tilos") String stream, @RequestParam(required = false) String type, HttpServletResponse resp) throws IOException {
        resp.setHeader("Content-Type", "audio/x-mpegurl; charset=iso-8859-2");

        String result = m3uService.lastWeek(stream, type);
        try (OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(), Charset.forName("ISO-8859-2"))) {
            writer.write(result);
        }
        return result;
    }


}
