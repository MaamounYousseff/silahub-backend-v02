import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.Commit;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = com.example.feed.FeedConfig.class)
@ComponentScan("com.example.feed")
public class FeedRepoTest
{
    @Autowired
    private FeedRepo feedRepo;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    @Commit
    void saveFeedPost() {

        UUID postId = UUID.fromString("0162e9dc-77a6-4c33-bd72-d037941bc1b4");
        UUID creatorId = UUID.fromString("68a54deb-07f3-458a-80ab-644430ce833b");

        long createdAtEpoch = 1754388866450L;  // converted created_at

        FeedPost feedPost = FeedPost.builder()
                .postId(postId)
                .creatorId(creatorId)
                .timeStamp(createdAtEpoch)
                .videoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                .creatorLogoUrl("https://picsum.photos/200/300")
                .creatorName("Test Post With Real URLs")
                .thumbnailUrl("https://picsum.photos/200/300")
                .ImageUrls("https://picsum.photos/300/400,https://via.placeholder.com/350.png")
                .whatsapNumber("enable")
                .lontitude(0f)
                .latitude(0f)
                .tempTotalLike(2L)
                .tempTotalUpvote(1L)
                .tempTotalClick(2L)
                .tempTotalWatchTime(1860L)
                .scoreUpdateCount(1L)
                .boostedAt(1760007395121L)
                .build();

        this.feedRepo.save(feedPost);
    }


}
