package com.example.interaction.api;

import com.example.interaction.domain.model.PostInteraction;
import com.example.interaction.domain.repo.PostInteractionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class PostInteractionPortImpl implements  PostInteractionPort{

    @Autowired
    private PostInteractionRepo postInteractionRepo;

    @Override
    public PostInteractionCreatedDto postCreated(UUID postId) {

        Optional<PostInteraction> postInteractionOptional = this.postInteractionRepo.findById(postId);

        PostInteraction postInteraction;

        if(PostInteraction.exist(postInteractionOptional)){
            postInteraction = postInteractionOptional.get();
            postInteraction.setBoostedAt(OffsetDateTime.now());
        }
        else{
             postInteraction = PostInteraction.builder()
                    .postId(postId)
                    .totalClicks(0L)
                    .totalLikes(0L)
                    .totalUpvotes(0L)
                    .totalViews(0L)
                    .totalWatchSeconds(0L)
                    .build();
        }

        this.postInteractionRepo.save(postInteraction);
        return fromPostInteraction(postInteraction);
    }

    private PostInteractionCreatedDto fromPostInteraction(PostInteraction postInteraction)
    {
        PostInteractionCreatedDto postInteractionCreatedDto = new PostInteractionCreatedDto(postInteraction.getBoostedAt().toEpochSecond());
        return postInteractionCreatedDto;
    }

}
