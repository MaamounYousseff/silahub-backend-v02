package com.example.feed.web;

import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.logic.FeedService;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


///api/v0/feed
@RestController
@RequestMapping("/api/v0/feed")
@CrossOrigin(origins = "http://localhost:5173")
public class FeedRestController
{
    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<SilahubResponse> fetchFeed (@RequestParam int offset)
    {
        List<FeedPost> feed= this.feedService.getFeed(offset);
        return ResponseEntity.ok(SilahubResponseUtil.success(feed,"Fetch Feed Successfully",Map.of()));
    }


    @GetMapping("/feedDetails")
    public ResponseEntity<SilahubResponse> getPostDetails (@RequestParam UUID postId)
    {
        FeedPost feed= this.feedService.getFeed(postId);
        return ResponseEntity.ok(SilahubResponseUtil.success(feed,"Fetch Feed Post Details Successfully",Map.of()));
    }

}
