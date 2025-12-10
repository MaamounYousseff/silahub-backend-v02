package com.example.feed.infrastructure.event;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.example.shared.domain.event.post.PostEventListener;
import com.example.shared.domain.event.post.EventPostCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PostFeedEventListenerImpl implements PostEventListener
{

    @Autowired
    private FeedRepo feedRepo;

//    @Not Tested
    @Override
    @EventListener
    public void onPostCreated(EventPostCreated eventPostCreated) {
        System.out.println("Post receive post created");

        FeedPost feedPost = fromEventPostCreated(eventPostCreated);
        this.feedRepo.save(feedPost);

    }

    private FeedPost fromEventPostCreated(EventPostCreated eventPostCreated) {
        return FeedPost.builder()
                .postId(eventPostCreated.getPostId())
                .creatorId(eventPostCreated.getCreatorId())
                .timeStamp(eventPostCreated.getTimeStamp())
                .videoUrl(eventPostCreated.getVideoUrl())
                .creatorLogoUrl(eventPostCreated.getCreatorLogoUrl())
                .creatorName(eventPostCreated.getCreatorName())
                .thumbnailUrl(eventPostCreated.getThumbnailUrl())
                .ImageUrls(eventPostCreated.getImageUrls())
                .whatsapNumber(eventPostCreated.getWhatsapNumber())
                .lontitude(eventPostCreated.getLontitude())
                .latitude(eventPostCreated.getLatitude())
                // Builder.Default fields are automatically set, no need to set them here
                .build();
    }

}
