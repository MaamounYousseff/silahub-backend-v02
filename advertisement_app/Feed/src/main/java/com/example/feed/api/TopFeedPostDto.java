package com.example.feed.api;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class TopFeedPostDto
{
    List<UUID> topPosts;

    public  TopFeedPostDto()
    {
        this.topPosts = new ArrayList<>();
    }
}
