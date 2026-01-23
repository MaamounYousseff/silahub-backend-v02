import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.Commit;

import java.util.UUID;


@SpringBootTest(classes = com.example.feed.FeedConfig.class)
@ComponentScan("com.example.feed")
public class FeedRepoTest
{
    @Autowired
    private FeedRepo feedRepo;
    @Autowired
    private MongoTemplate mongoTemplate;





}
