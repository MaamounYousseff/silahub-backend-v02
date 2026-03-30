package com.example.feed.logic;

import com.example.feed.api.TopFeedPostDto;
import com.example.feed.api.TopFeedPostPort;
import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.exception.FeedPostNotExistException;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.example.feed.domain.repo.UserFeedPostHistoryRepo;
import com.example.shared.interaction.InteractionEventToggleLike;
import com.example.shared.interaction.InteractionEventToggleUpvote;
import com.example.shared.security.CurrentUserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FeedService
{
    @Autowired
    private CurrentUserContext userContext;
    @Autowired
    private UserFeedPostHistoryRepo userFeedPostHistoryRepo;
    @Autowired
    private TopFeedPostPort topFeedPostPort;
    @Autowired
    private FeedRepo feedRepo;

    public static final int FEED_PAGING_SIZE = 3;


    public List<FeedPost> getFeed(AtomicInteger atomicOffset) {
        UUID expolrerId = userContext.getUserId();

        List<UUID> postsSeenByUserList = userFeedPostHistoryRepo.getHistory(expolrerId);

//        tight coupling with scoring context
        List<UUID> unseenPosts = new ArrayList<>();

        getNTopPosts(postsSeenByUserList, unseenPosts , atomicOffset);

        // Keep only the last 3 posts
        if(unseenPosts.size() > FEED_PAGING_SIZE)
            unseenPosts = unseenPosts.subList(Math.max(unseenPosts.size() - FEED_PAGING_SIZE, 0), unseenPosts.size());

        // Get FeedPost from NOSQL
        List<FeedPost> feedPosts     = new ArrayList<>();
        for(int i =0 ; i < unseenPosts.size(); i++)
        {
            Optional<FeedPost> feedPostOptional = this.feedRepo.findByPostId(unseenPosts.get(i));
            if(!FeedPost.exist(feedPostOptional))
                continue;

            feedPosts.add(feedPostOptional.get());
        }

        // Update user post history with new post IDs
        List<UUID> newPostIds = unseenPosts;

        List<UUID> newSeenPosts = new ArrayList<>(newPostIds);
        newSeenPosts.addAll(postsSeenByUserList);

        userFeedPostHistoryRepo.saveHistory(expolrerId, newSeenPosts);

        return feedPosts;
    }


    private void  getNTopPosts( List<UUID> postsSeenByUserList, List<UUID> unseenPosts,  AtomicInteger atomicOffset  )
    {
//        INFO : Basecase unseen posts should get the same or bigger size then TopPost List

        if(unseenPosts.size() >= FEED_PAGING_SIZE)
            return ;

//        tightly with scoring
        Optional<TopFeedPostDto> topPostsOpt = topFeedPostPort.getNTopPosts(atomicOffset.get());

        //case when we have a limit but there was one or two more post <      FEED_PAGING_SIZE
        if(topPostsOpt.get().getTopPosts().size() == 0  && unseenPosts.size() != 0)
            return;

        if(topPostsOpt.get().getTopPosts().size() == 0 && unseenPosts.size() == 0)
            throw new FeedPostLimitExceededException();

        TopFeedPostDto topPosts = topPostsOpt.get();


        // Filter posts the user hasn't seen
        List<UUID> filteredPosts = topPosts.getTopPosts().stream()
                .filter(postId -> !isPostSeenBefore(postId, postsSeenByUserList))
                .toList();

        unseenPosts.addAll(filteredPosts);

        if (unseenPosts.size() < FEED_PAGING_SIZE)
        {
            atomicOffset.set(atomicOffset.get() + 1 );
            getNTopPosts(postsSeenByUserList,unseenPosts,atomicOffset);
        }

    }



    /**
     * Check if a user has already seen a post.
     */
    private boolean isPostSeenBefore(UUID postId, List<UUID> postsSeen) {
        if(postsSeen.size() == 0) return false;
        return postsSeen.contains(postId);
    }
//    End Feed



    public FeedPost getFeed(UUID postId)
    {
        Optional<FeedPost> feedPost= this.feedRepo.findByPostId(postId);
        if(feedPost.isEmpty())
            throw new FeedPostNotExistException();

        return feedPost.get();
    }

    public void processLike(InteractionEventToggleLike event)
    {
        switch (event.getToggleLikeAction())
        {
            case INSERT, UPDATE_LIKED -> {
                boolean isUpdated = this.feedRepo.addLike(event.getPostId(), event.getExplorerId());

                if(!isUpdated)
                {
                    log.info("Stop process add Like in Feed");
                    return;
                }
            }

            case UPDATE_UNLIKED -> {
                boolean isUpdated = this.feedRepo.removeLike(event.getPostId(), event.getExplorerId());
                if(!isUpdated)
                {
                    log.info("Stop process add Like in Feed");
                    return;
                }
            }

        }
    }

    public void processUpvote(InteractionEventToggleUpvote event)
    {
//        in the case of insert we need to update boostedAt eventToggleUpvote.getBoostedAt()
        switch (event.getToggleUpvoteState())
        {
            case INSERT -> {
                boolean isUpdated = this.feedRepo.addUpvote(event.getPostId(), event.getPromoterId(), event.getBoostedAt());
                if(!isUpdated)
                {
                    log.info("Stop process add upvote in Feed");
                    return;
                }
            }
            case UPDATE_ADDED_UPVOTE -> {
                boolean isUpdated = this.feedRepo.updateUpvote(event.getPostId(), event.getPromoterId());
                if(!isUpdated)
                {
                    log.info("Stop process update upvote in Feed");
                    return;
                }
            }

            case UPDATE_REMOVED_UPVOTE -> {
                boolean isUpdated = this.feedRepo.removeUpvote(event.getPostId(), event.getPromoterId());
                if(!isUpdated)
                {
                    log.info("Stop process remove upvote in Feed");
                    return;
                }
            }

        }
    }


    public boolean addNewAsset(UUID postId, String assetUrl, String assetType)
    {
        this.feedRepo.addNewAsset(postId, assetUrl, assetType);
        return true;
    }

}
