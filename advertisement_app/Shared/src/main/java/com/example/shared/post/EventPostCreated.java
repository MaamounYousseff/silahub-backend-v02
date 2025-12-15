package com.example.shared.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventPostCreated
{
    private UUID postId ;
    private String videoUrl;
    private String thumbnailUrl;
    private List<String> ImageUrls;
    private UUID creatorId;
    private Long timeStamp;
    private Long boostedAt;


}
