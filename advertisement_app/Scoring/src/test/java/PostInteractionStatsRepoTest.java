import com.example.scoring.domain.model.PostInteractionStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(classes = {com.example.scoring.ScoringConfig.class})
@ComponentScan(basePackages = "com.example.scoring")
class PostInteractionStatsRepoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @Commit
    void savePost() {
        UUID postId = UUID.fromString("0162e9dc-77a6-4c33-bd72-d037941bc1b4");

        PostInteractionStats stats = PostInteractionStats.builder()
                .postId(postId)
                .tempTotalLike(2L)
                .tempTotalUpvote(1L)
                .tempTotalClick(2L)
                .tempTotalWatchTime(1860L)
                .scoreUpdateCount(1L)
                .boostedAt(1760007395121L) // converted from timestamp
                .build();

        // insert
        mongoTemplate.save(stats, "post_interaction_stats");

        // fetch
        Query query = new Query(Criteria.where("_id").is(postId));
        PostInteractionStats result =
                mongoTemplate.findOne(query, PostInteractionStats.class);

        assertNotNull(result);
        assertEquals(2L, result.getTempTotalLike());
        assertEquals(1L, result.getTempTotalUpvote());
        assertEquals(2L, result.getTempTotalClick());
        assertEquals(1860L, result.getTempTotalWatchTime());
        assertEquals(1L, result.getScoreUpdateCount());
        assertEquals(1760007395121L, result.getBoostedAt());
    }
}
