package com.example.scoring.infrastructure.event;

import com.example.scoring.domain.model.PostInteractionStats;
import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import com.example.scoring.domain.repo.PostInteractionStatsRepository;
import com.example.shared.post.PostEventListener;
import com.example.shared.post.EventPostCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PostScoreEventListenerImpl implements PostEventListener
{
    @Autowired
    private BucketRepository bucketRepository;
    @Autowired
    private PostInteractionStatsRepository postInteractionStatsRepository;

//@    Not Tested
    @Override
    @EventListener
    public void onPostCreated(EventPostCreated eventPostCreated) {
        PostScoreBucket postScoreBucket = new PostScoreBucket(eventPostCreated.getPostId());
        this.bucketRepository.addPostScore(postScoreBucket);

        PostInteractionStats postInteractionStats = PostInteractionStats.builder()
                .postId(eventPostCreated.getPostId())
                .boostedAt(eventPostCreated.getTimeStamp())
                .build();

        this.postInteractionStatsRepository.save(postInteractionStats);
    }
}
