import com.example.feed.domain.repo.UserFeedPostHistory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Commit;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = com.example.feed.FeedConfig.class)
@ComponentScan("com.example.feed")
public class UserFeedPostHistoryTest

{

    @Autowired
    private UserFeedPostHistory userFeedPostHistory;


    @Test
    @Commit
    void testSaveHistoryWithCreatorAndSinglePost() {

        // given
        UUID explorerId = UUID.fromString("68a54deb-07f3-458a-80ab-644430ce833b");
        UUID postId = UUID.fromString("0162e9dc-77a6-4c33-bd72-d037941bc1b4");

        List<UUID> postList = List.of(postId);

       this.userFeedPostHistory.saveHistory(explorerId, postList);
    }


    @Test
    @Commit
    void testgetHistory() {

        // given
        UUID explorerId = UUID.fromString("68a54deb-07f3-458a-80ab-644430ce833b");
        List<UUID> postIds= this.userFeedPostHistory.getHistory(explorerId);
        return;


    }
}
