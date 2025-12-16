package com.example.feed.web;

import com.example.feed.domain.model.FeedPost;
import com.example.feed.logic.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


///api/v0/feed
@RestController
@RequestMapping("/api/v0/feed")
public class FeedController
{
    @Autowired
    private FeedService feedService;

    @GetMapping("/test")
    public String fetchFeed (@RequestParam int offset)
    {
        List<FeedPost> feed= this.feedService.getFeed(offset);
        return feed.toString();
    }


}
