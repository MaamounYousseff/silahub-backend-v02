package com.example.feed.logic;

import com.example.feed.api.TopFeedPostDto;
import com.example.feed.api.TopFeedPostPort;
import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.exception.FeedPostNotExistException;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.example.feed.domain.repo.UserFeedPostHistoryRepo;
import com.example.shared.security.CurrentUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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


    public List<FeedPost> getFeed(int offset) {
        UUID expolrerId = userContext.getUserId();

        List<UUID> postsSeenByUserList = userFeedPostHistoryRepo.getHistory(expolrerId);

//        tight coupling with scoring context
        List<UUID> unseenPosts = new ArrayList<>();

        getNTopPosts(postsSeenByUserList, unseenPosts , offset);

        // Keep only the last 3 posts
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


    private void getNTopPosts( List<UUID> postsSeenByUserList, List<UUID> unseenPosts,  int offset  )
    {
//        INFO : Basecase unseen posts should get the same or bigger size then TopPost List

        if(unseenPosts.size() >= FEED_PAGING_SIZE)
            return;

//        tightly with scoring
        Optional<TopFeedPostDto> topPostsOpt = topFeedPostPort.getNTopPosts(offset);
        if(topPostsOpt.isEmpty() && unseenPosts.size() == 0)
            throw new FeedPostLimitExceededException();

        TopFeedPostDto topPosts = topPostsOpt.get();


        if(unseenPosts.size() > 0)
            unseenPosts.addAll(topPosts.getTopPosts());

        // Filter posts the user hasn't seen
        List<UUID> filteredPosts = topPosts.getTopPosts().stream()
                .filter(postId -> !isPostSeenBefore(postId, postsSeenByUserList))
                .toList();

        unseenPosts.addAll(filteredPosts);

        if (unseenPosts.size() < FEED_PAGING_SIZE)
            getNTopPosts(postsSeenByUserList,unseenPosts,++offset);
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

}
