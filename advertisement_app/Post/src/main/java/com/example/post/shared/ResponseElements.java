package com.example.post.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseElements {
    @JsonProperty("x-amz-request-id")
    private String xAmzRequestId;

    @JsonProperty("x-amz-id-2")
    private String xAmzId2;
}
