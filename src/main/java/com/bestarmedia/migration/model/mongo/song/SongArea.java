package com.bestarmedia.migration.model.mongo.song;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "song_area")
@AllArgsConstructor
@NoArgsConstructor
public class SongArea extends SongAuditing {

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
