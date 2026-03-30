package com.example.media_ingestion.sqsevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseElements {
    @JsonProperty("x-amz-request-id")
    private String xAmzRequestId;

    @JsonProperty("x-amz-id-2")
    private String xAmzId2;
}
