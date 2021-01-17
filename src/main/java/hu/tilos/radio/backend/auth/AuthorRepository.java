package hu.tilos.radio.backend.auth;

import hu.tilos.radio.backend.data.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthorRepository extends MongoRepository<Author, String> {
}
