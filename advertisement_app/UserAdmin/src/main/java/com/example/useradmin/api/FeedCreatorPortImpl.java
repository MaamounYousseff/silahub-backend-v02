package com.example.useradmin.api;

import com.example.useradmin.domain.model.User;
import com.example.useradmin.domain.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class FeedCreatorPortImpl implements FeedCreatorPort {

    @Autowired
    private UserRepository userRepository;

    @Override
    public FeedCreatorDto getCreatorProfile(UUID userId) {
        Optional<FeedCreatorDto> userOptional = this.userRepository.getCreatorProfile(userId);
        if(userOptional.isEmpty())
            return null;

        FeedCreatorDto creatorDto = userOptional.get();
        return creatorDto;
    }
}
