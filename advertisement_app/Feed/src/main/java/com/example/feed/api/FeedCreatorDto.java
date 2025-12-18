package com.example.feed.api;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FeedCreatorDto
{
    private UUID creatorId;
    private String creatorName;
    private String creatorLogoUrl;
    private String creatorUsername;
    private Double longitude;
    private Double latitude;
    private String whatsappNumber;


    public static boolean exist(Optional<FeedCreatorDto> feedCreatorDto)
    {
        if(feedCreatorDto.isEmpty())
            return false;
        return true;
    }


}
