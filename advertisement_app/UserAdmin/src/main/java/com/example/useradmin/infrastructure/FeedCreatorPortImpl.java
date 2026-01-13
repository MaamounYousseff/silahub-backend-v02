package com.example.useradmin.infrastructure;

import com.example.feed.api.FeedCreatorDto;
import com.example.feed.api.FeedCreatorPort;
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

    public Optional<FeedCreatorDto> getCreatorProfile(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty())
            return Optional.empty();

        User user = userOptional.get();

        return Optional.of(
                FeedCreatorDtoMapper.fromUser(user)
        );
    }



}
