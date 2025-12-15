package com.example.useradmin.api;

import com.example.useradmin.domain.model.User;

public class Mapper
{
    public static FeedCreatorDto fromUser(User user)
    {
        return FeedCreatorDto.builder()
                .creatorId(user.getId())
                .creatorName(user.getFirstName() + " " + user.getLastName())
                .creatorUsername(user.getUsername())
                .longitude(user.getLongitude())
                .latitude(user.getLatitude())
                .creatorLogoUrl(user.getLogoUrl())
                .whatsappNumber(user.getWhatsappNumber())
                .build();
    }
}
