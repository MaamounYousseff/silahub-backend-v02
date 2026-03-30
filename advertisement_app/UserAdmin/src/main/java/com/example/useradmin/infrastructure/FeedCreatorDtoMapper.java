package com.example.useradmin.infrastructure;

import com.example.feed.api.FeedCreatorDto;
import com.example.useradmin.domain.model.User;

public class FeedCreatorDtoMapper
{
    public static FeedCreatorDto fromUser(User user)
    {
        return FeedCreatorDto.builder()
                .creatorId(user.getId())
                .creatorUsername(user.getUsername())
                .creatorName(user.getFirstName() + " "+ user.getLastName())
                .creatorLogoUrl(user.getLogoUrl())
                .whatsappNumber(user.getWhatsappNumber())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }
}
