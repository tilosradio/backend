package hu.tilos.radio.backend.content.page;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PageRepository extends MongoRepository<Page, String> {

    Page findByAlias(String alias);

    Page findByAliasOrId(String alias, String id);

    Long deletePageByAlias(String alias);

}
