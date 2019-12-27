package hu.tilos.radio.backend.content.news;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {
    News findByAlias(String alias);

    Long deleteNewsByAlias(String alias);

    News findByAliasOrId(String alias, String alias1);

    List<News> findFirst10SortedByCreated();
}
