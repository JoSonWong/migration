package com.bestarmedia.migration.model.mongo.vod;


import com.bestarmedia.migration.model.mongo.Auditing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "vod_area")
@AllArgsConstructor
@NoArgsConstructor
public class VodArea extends Auditing {

    @Field(value = "code")
    private int code;

    @Field(value = "name")
    private String name;

    @Field(value = "parent_code")
    private int parentCode;

    @Field(value = "sort")
    private int sort;

    @Field(value = "is_show")
    private int isShow;
}
