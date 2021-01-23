package hu.tilos.radio.backend.auth;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static hu.tilos.radio.backend.MongoTestUtil.dropCollection;
import static hu.tilos.radio.backend.MongoTestUtil.loadTo;


@RunWith(SpringRunner.class)
@SpringBootTest()
public class UserServiceTest {

    @Inject
    UserService userService;

    @Inject
    DB mongodb;

    @Test
    public void getUser() throws Exception {
        dropCollection(mongodb, "user", "show", "author");
        String showId = loadTo(mongodb, "show", "show-sellaction.json");
        String authorId = loadTo(mongodb, "author", "author-beugrodj.json", showId);
        loadTo(mongodb, "user", "user-hanglemezbarat.json", authorId);

        final BasicDBObject selector = new BasicDBObject("_id", new ObjectId(showId));
        final BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("contributors.0.ref", new DBRef("author", authorId)));
        final WriteResult show = mongodb.getCollection("show").update(selector, update);

        final UserInfo user = userService.getUser("hanglemezbarat");

        Assert.assertEquals("sell-action-avagy-barati-hangok-lemezekrol", user.getAuthor().getContributions().get(0).getShow().getAlias());

        Assert.assertEquals(showId, user.getAuthor().getContributions().get(0).getShow().getId());
    }
}