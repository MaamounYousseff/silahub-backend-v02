package com.example.useradmin.api;

import com.example.shared.useradmin.CreatorNotFoundException;
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
        Optional<FeedCreatorDto> creatorDtoOptional = this.userRepository.getCreatorProfile(userId);
        if(!FeedCreatorDto.exist(creatorDtoOptional))
            throw new CreatorNotFoundException();

        FeedCreatorDto creatorDto = creatorDtoOptional.get();
        return creatorDto;
    }
}
