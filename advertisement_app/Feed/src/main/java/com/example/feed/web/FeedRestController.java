package com.example.feed.web;

import com.example.feed.domain.exception.FeedPostLimitExceededException;
import com.example.feed.domain.model.FeedPost;
import com.example.feed.infrastructure.CookieService;
import com.example.feed.logic.FeedService;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


///api/v0/feed
@RestController
@RequestMapping("/api/v0/feed")
@CrossOrigin(origins = "http://localhost:5173")
public class FeedRestController
{
    @Autowired
    private FeedService feedService;
    @Autowired
    private CookieService cookieService;

    @GetMapping
    public ResponseEntity<SilahubResponse> fetchFeed ()
    {
//        get cookie
        Optional<Integer> offset = cookieService.getFeedOffset();
        if(offset.isEmpty())
             offset = Optional.of(cookieService.createCookie());

        AtomicInteger atomicInteger = new AtomicInteger();
        atomicInteger.set(offset.get());

//        logic
        List<FeedPost> feed= this.feedService.getFeed(atomicInteger);

//        update the cookie value for the  web
        this.cookieService.updateOffset(atomicInteger.get() + 1);

        return ResponseEntity.ok(SilahubResponseUtil.success(feed,"Fetch Feed Successfully",Map.of()));
    }


    @GetMapping("/feedDetails")
    public ResponseEntity<SilahubResponse> getPostDetails (@RequestParam UUID postId)
    {
        FeedPost feed= this.feedService.getFeed(postId);
        return ResponseEntity.ok(SilahubResponseUtil.success(feed,"Fetch Feed Post Details Successfully",Map.of()));
    }

}
