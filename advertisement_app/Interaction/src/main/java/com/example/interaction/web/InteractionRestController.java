package com.example.interaction.web;

import com.example.interaction.logic.InteractionService;
import com.example.shared.SilahubResponse;
import com.example.shared.SilahubResponseUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/interactions/v0")
public class InteractionRestController {

    @Autowired
    private  InteractionService interactionService;

    // POST /api/interactions/click/{feedPostId}
    @PostMapping("/click/{feedPostId}")
    public ResponseEntity<SilahubResponse> clickPost(@PathVariable UUID feedPostId) throws JsonProcessingException {
        interactionService.feedPostClicked(feedPostId);
        return ResponseEntity.ok(SilahubResponseUtil.success("","Post Liked", Map.of()));
    }
    // POST /api/interactions/watch/{feedPostId}
    @PostMapping("/watch/{feedPostId}")
    public ResponseEntity<SilahubResponse> watchPost(@PathVariable UUID feedPostId, @RequestParam("watchTime") Long watchTime) throws JsonProcessingException {
        interactionService.feedPostWatched(feedPostId, watchTime);
        return ResponseEntity.ok(SilahubResponseUtil.success("","Post Watched", Map.of()));
    }

    // POST /api/interactions/like/{feedPostId}
    @PostMapping("/like/{feedPostId}")
    public ResponseEntity<SilahubResponse> likePost(@PathVariable UUID feedPostId) throws JsonProcessingException {
        interactionService.feedPostLikeToggle(feedPostId);
        return ResponseEntity.ok(SilahubResponseUtil.success("","Post Liked", Map.of()));
    }

    // POST /api/interactions/upvote/{feedPostId}
    @PostMapping("/upvote/{feedPostId}")
    public ResponseEntity<SilahubResponse> upvotePost(@PathVariable UUID feedPostId) throws JsonProcessingException {
        interactionService.feedPostUpvoteToggle(feedPostId);
        return ResponseEntity.ok(SilahubResponseUtil.success("","Post Upvoted", Map.of()));
    }


}
