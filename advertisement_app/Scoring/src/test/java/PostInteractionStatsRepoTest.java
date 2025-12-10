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
    void shouldInsertDocumentUsingManualQuery() {
        // given — exact data from your request
        UUID postId = UUID.fromString("0162e9dc-77a6-4c33-bd72-d037941bc1b4");

        PostInteractionStats stats = PostInteractionStats.builder()
                .postId(postId)
                .tempTotalLike(2L)         // totalLikes = 2
                .tempTotalUpvote(1L)       // totalUpvotes = 1
                .tempTotalClick(2L)        // totalClicks = 2
                .tempTotalWatchTime(780L)  // totalWatchTime = 780
                .scoreUpdateCount(1L)      // totalViews = 1  (your field name)
                .boostedAt(1760120195121L) // boostedAt
                .build();

        // when → insert manually
        mongoTemplate.insert(stats, "post_interaction_stats");

        // then → fetch manually using _id
        Query query = new Query(Criteria.where("_id").is(postId));
        PostInteractionStats result = mongoTemplate.findOne(query, PostInteractionStats.class);

        assertNotNull(result);
        assertEquals(2L, result.getTempTotalLike());
        assertEquals(1L, result.getTempTotalUpvote());
        assertEquals(2L, result.getTempTotalClick());
        assertEquals(780L, result.getTempTotalWatchTime());
        assertEquals(1L, result.getScoreUpdateCount());
        assertEquals(1760120195121L, result.getBoostedAt());
    }
}
