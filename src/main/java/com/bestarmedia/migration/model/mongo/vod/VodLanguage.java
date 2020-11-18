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
@Document(value = "vod_language")
@AllArgsConstructor
@NoArgsConstructor
public class VodLanguage extends Auditing {

    @Field(value = "code")
    private Integer code;

    @Field(value = "name")
    private String name;

    @Field(value = "parent_code")
    private Integer parentCode;

    @Field(value = "sort")
    private Integer sort;

    @Field(value = "is_show")
    private Integer isShow;
}
