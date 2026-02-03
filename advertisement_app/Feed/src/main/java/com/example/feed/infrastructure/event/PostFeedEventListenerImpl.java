package com.example.feed.infrastructure.event;

import com.example.feed.api.FeedCreatorDto;
import com.example.feed.api.FeedCreatorPort;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.domain.repo.FeedRepo;
import com.example.shared.post.PostEventListener;
import com.example.shared.post.EventPostCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
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

        Optional<FeedCreatorDto> feedCreatorDtoOptional = feedCreatorPort.getCreatorProfile(eventPostCreated.getCreatorId());
        if(feedCreatorDtoOptional.isEmpty())
        {
            log.error("feed creator unavailable : "+eventPostCreated.getCreatorId());
            return;
        }
        FeedCreatorDto feedCreatorDto =  feedCreatorDtoOptional.get();

        feedPost.setVideoUrl(eventPostCreated.getVideoUrl());
        feedPost.setCreatorLogoUrl(feedCreatorDto.getCreatorLogoUrl());
        feedPost.setCreatorName(feedCreatorDto.getCreatorName());
        feedPost.setLongitude(feedCreatorDto.getLongitude());
        feedPost.setLatitude(feedCreatorDto.getLatitude());
        feedPost.setWhatsappNumber(feedCreatorDto.getWhatsappNumber());
        feedPost.setStatus("active");

        feedRepo.update( feedPost );

        return;
    }


    private FeedPost fromEventPostCreated(EventPostCreated eventPostCreated) {
        return FeedPost.builder()
                .postId(eventPostCreated.getPostId())
                .creatorId(eventPostCreated.getCreatorId())
                .videoUrl(eventPostCreated.getVideoUrl())
                .boostedAt(eventPostCreated.getBoostedAt())
                .timeStamp(eventPostCreated.getTimeStamp())
                .build();
    }

}
