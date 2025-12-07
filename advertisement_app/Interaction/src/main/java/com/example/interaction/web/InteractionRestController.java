package com.example.interaction.web;

import com.example.interaction.logic.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/interactions/v0")
public class InteractionRestController {

    @Autowired
    private  InteractionService interactionService;

    // POST /api/interactions/click/{feedPostId}
    @PostMapping("/click/{feedPostId}")
    public void clickPost(@PathVariable UUID feedPostId) {
        interactionService.feedPostClicked(feedPostId);
    }

    // POST /api/interactions/like/{feedPostId}
    @PostMapping("/like/{feedPostId}")
    public void likePost(@PathVariable UUID feedPostId) {
        interactionService.feedPostLiked(feedPostId);
    }

    // POST /api/interactions/upvote/{feedPostId}
    @PostMapping("/upvote/{feedPostId}")
    public void upvotePost(@PathVariable UUID feedPostId) {
        interactionService.feedPostUpvoted(feedPostId);
    }

    // POST /api/interactions/watch/{feedPostId}
    @PostMapping("/watch/{feedPostId}")
    public void watchPost(@PathVariable UUID feedPostId) {
        interactionService.feedPostWatched(feedPostId);
    }
}
