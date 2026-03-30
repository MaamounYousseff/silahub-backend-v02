package com.example.media_ingestion.sqsevent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class S3EventNotification
{
    @JsonProperty("Records")
    private List<S3EventRecord> records;
}
