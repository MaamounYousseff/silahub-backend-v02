package com.example.feed.web;

import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.logic.FeedService;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


///api/v0/feed
@RestController
@RequestMapping("/api/v0/feed")
public class FeedRestController
{
    @Autowired
    private FeedService feedService;

    @GetMapping("/test")
    public ResponseEntity<SilahubResponse> fetchFeed (@RequestParam int offset)
    {
        List<FeedPost> feed= this.feedService.getFeed(offset);
        return ResponseEntity.ok(SilahubResponseUtil.success(feed,"Fetch Feed Successfully",Map.of()));
    }


    @GetMapping("/hi")
    public void dsadsa()
    {
        throw new FeedPostLimitExceededException();
    }


}
