package com.example.media_ingestion.sqsevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class S3EventRecord
{
    @JsonProperty("eventVersion")
    private String eventVersion;
    private String eventSource;
    private String awsRegion;
    private String eventTime;
    private String eventName;
    private UserIdentity userIdentity;
    private RequestParameters requestParameters;
    private ResponseElements responseElements;
    private S3Entity s3;
}
