package hu.tilos.radio.backend.episode;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EpisodeRepository extends MongoRepository<EpisodeData, String> {
}
