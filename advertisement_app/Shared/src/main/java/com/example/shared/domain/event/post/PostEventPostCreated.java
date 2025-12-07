package com.example.shared.domain.event.post;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostEventPostCreated
{
    private UUID postId ;
    private UUID creatorId;
    private Long timeStamp;
}
