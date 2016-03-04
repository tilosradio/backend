package hu.tilos.radio.backend;

import hu.tilos.radio.backend.episode.EpisodeRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class EpisodeRepositoryMockFactory {
    @Bean
    public EpisodeRepository getRepository() {
        return Mockito.mock(EpisodeRepository.class);
    }
}
