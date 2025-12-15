package com.example.useradmin.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
}
