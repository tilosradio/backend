package hu.tilos.radio.backend;

import hu.tilos.radio.backend.converters.HTMLSanitizer;
import org.dozer.ConfigurableCustomConverter;

import javax.inject.Inject;

public class EpisodeContentCleaner implements ConfigurableCustomConverter {

    @Inject
    HTMLSanitizer sanitizer;

    @Override
    public void setParameter(String parameter) {

    }

    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        return sanitizer.clean((String) sourceFieldValue);
    }
}

