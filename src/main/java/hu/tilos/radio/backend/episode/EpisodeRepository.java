package hu.tilos.radio.backend.episode;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EpisodeRepository extends MongoRepository<EpisodeData, String> {
}
