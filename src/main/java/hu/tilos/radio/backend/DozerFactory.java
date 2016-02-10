package hu.tilos.radio.backend;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import hu.tilos.radio.backend.converters.*;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


@Component
public class DozerFactory {

    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(DozerFactory.class);

    @Inject
    DB db;

    private DozerBeanMapper mapper;

    public static BasicDBObject createDbObject() {
        return new BasicDBObject();
    }

    public DozerBeanMapper mapperFactory() {
        return mapper;
    }

    @PostConstruct
    public void init() {

        List<String> mappingFiles = detectMappings();
        mappingFiles.add("dozer.xml");
        mapper = new DozerBeanMapper(mappingFiles);

    }


    protected List<String> detectMappings() {
        List<String> result = new ArrayList<>();
        LOG.debug("searching for dozer.file");
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("dozer.file");
            if (resources.hasMoreElements()) {
                LOG.warn("No dozer.file on the classpath");
            }
            while (resources.hasMoreElements()) {
                InputStream is = resources.nextElement().openStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\n");
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.trim().length() > 0) {
                        result.add(line);
                        LOG.info("Adding dozer definition: " + line);
                    }
                }
                is.close();
            }
        } catch (Exception e) {
            LOG.error("Error during dozer file detenction", e);
        }
        return result;

    }

    @Bean
    public DozerBeanMapper get() {
        if (mapper == null) {
            init();
        }
        return mapper;
    }
}
