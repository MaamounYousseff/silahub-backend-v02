package com.example.scoring.infrastructure;

import com.example.feed.api.TopFeedPostDto;
import com.example.feed.api.TopFeedPostPort;
import com.example.scoring.domain.model.PostScoreBucket;
import com.example.scoring.domain.repo.BucketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopFeedPostPortImpl implements TopFeedPostPort
{

    @Autowired
    private BucketRepository bucketRepository;

    @Override
    public Optional<TopFeedPostDto> getNTopPosts(int offset) {

        List<PostScoreBucket> postScoreBucket = this.bucketRepository.fetchTopPosts(offset);
        return fromPostScoreBucketList(postScoreBucket);
    }

    private Optional<TopFeedPostDto> fromPostScoreBucketList(List<PostScoreBucket> postScoreBucketList)
    {
        if(postScoreBucketList == null)
            return Optional.empty();

        TopFeedPostDto topFeedPostDto = new TopFeedPostDto();
        List<UUID> list = topFeedPostDto.getTopPosts();

        postScoreBucketList.forEach(postScoreBucket -> list.add(postScoreBucket.getPostId()));
        return Optional.of(topFeedPostDto);
    }
}
