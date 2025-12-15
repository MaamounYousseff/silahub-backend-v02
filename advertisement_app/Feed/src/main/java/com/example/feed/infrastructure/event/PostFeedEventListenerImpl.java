package com.example.feed.infrastructure.event;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.example.shared.post.PostEventListener;
import com.example.shared.post.EventPostCreated;
import com.example.useradmin.api.FeedCreatorDto;
import com.example.useradmin.api.FeedCreatorPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class PostFeedEventListenerImpl implements PostEventListener
{

    @Autowired
    private FeedRepo feedRepo;
    @Autowired
    private FeedCreatorPort feedCreatorPort;


//    @Not Tested
    @Override
    @EventListener
    public void onPostCreated(EventPostCreated eventPostCreated) {
        System.out.println("Post receive post created");

        FeedPost feedPost = fromEventPostCreated(eventPostCreated);

        FeedCreatorDto feedCreatorDto = feedCreatorPort.getCreatorProfile(eventPostCreated.getCreatorId());

        feedPost.setCreatorLogoUrl(feedCreatorDto.getCreatorLogoUrl());
        feedPost.setCreatorName(feedCreatorDto.getCreatorName());
        feedPost.setLontitude(feedCreatorDto.getLongitude());
        feedPost.setLatitude(feedCreatorDto.getLatitude());
        feedPost.setWhatsapNumber(feedCreatorDto.getWhatsappNumber());
        this.feedRepo.save(feedPost);
        return;
    }


    private FeedPost fromEventPostCreated(EventPostCreated eventPostCreated) {
        return FeedPost.builder()
                .postId(eventPostCreated.getPostId())
                .creatorId(eventPostCreated.getCreatorId())

                .videoUrl(eventPostCreated.getVideoUrl())
                .thumbnailUrl(eventPostCreated.getThumbnailUrl())
                .ImageUrls(eventPostCreated.getImageUrls())

                .boostedAt(eventPostCreated.getBoostedAt())
                .timeStamp(eventPostCreated.getTimeStamp())
                .build();
    }

}
