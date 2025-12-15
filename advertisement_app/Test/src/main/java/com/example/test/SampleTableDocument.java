package com.example.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "sample_table")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class SampleTableDocument {

    @Id
    private ObjectId _id;

    @Field("id")
    private Long id;

    @Field("col_1")
    private Long col1;

    @Field("col_2")
    private Long col2;

    @Field("col_3")
    private Long col3;

    @Field("col_4")
    private Long col4;

    @Field("col_5")
    private Long col5;

    @Field("col_6")
    private Long col6;

    @Field("col_7")
    private Long col7;

    @Field("col_8")
    private Long col8;

    @Field("col_9")
    private Long col9;

    @Field("col_10")
    private Long col10;

    @Field("col_11")
    private Long col11;

    @Field("col_12")
    private Long col12;

    @Field("col_13")
    private Long col13;

    @Field("col_14")
    private Long col14;

    @Field("col_15")
    private Long col15;

    @Field("col_16")
    private Long col16;

    @Field("col_17")
    private Long col17;

    @Field("col_18")
    private Long col18;

    @Field("col_19")
    private Long col19;

    @Field("col_20")
    private Long col20;
    // getters and setters
}
