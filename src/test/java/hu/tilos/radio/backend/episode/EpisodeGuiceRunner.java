package hu.tilos.radio.backend.episode;

import com.github.fakemongo.junit.FongoRule;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.mongodb.DB;
import hu.tilos.radio.backend.EpisodeDozerFactory;
import hu.tilos.radio.backend.FongoCreator;
import hu.tilos.radio.backend.GuiceRunner;
import hu.tilos.radio.backend.ValidatorProducer;
import hu.tilos.radio.backend.spark.GuiceConfigurationListener;
import org.dozer.DozerBeanMapper;

import javax.validation.Validator;

public class EpisodeGuiceRunner extends GuiceRunner {

    public EpisodeGuiceRunner(Object obj) {
        super(obj);
    }

    @Override
    public AbstractModule createInjector(FongoCreator creator, DB db) {
        return new AbstractModule() {

            @Override
            protected void configure() {
                this.bind(FongoRule.class).toInstance(creator.createRule());
                this.bind(DB.class).toInstance(db);
                this.bind(DozerBeanMapper.class).toProvider(EpisodeDozerFactory.class).asEagerSingleton();
                this.bind(Validator.class).toProvider(ValidatorProducer.class);
                this.bindListener(Matchers.any(), new GuiceConfigurationListener());
            }
        };
    }
}
