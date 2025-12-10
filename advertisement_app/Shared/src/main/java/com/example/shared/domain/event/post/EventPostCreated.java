package com.example.shared.domain.event.post;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EventPostCreated
{
    private UUID postId ;
    private UUID creatorId;
    private Long timeStamp;
    private String videoUrl;
    private String creatorLogoUrl;
    private String creatorName;
    private String thumbnailUrl;
    private String ImageUrls;
    private String whatsapNumber;
    private float lontitude;
    private float latitude;

}
