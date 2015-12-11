package hu.tilos.radio.backend;

import org.dozer.CustomConverter;

import javax.inject.Inject;
import java.util.Map;

public class EpisodeDozerFactory extends DozerFactory{

    @Inject
    EpisodeContentCleaner contentCleaner;

    @Override
    public Map<String, CustomConverter> createCustomConverters() {
        Map<String, CustomConverter> customConverters = super.createCustomConverters();
        customConverters.put("contentCleaner", contentCleaner);
        return customConverters;
    }

}
