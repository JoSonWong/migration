package com.bestarmedia.migration.model.mongo.song;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "song_versions_type")
@AllArgsConstructor
@NoArgsConstructor
public class SongVersionsType extends SongAuditing {

    @Field(value = "code")
    private int code;//id

    @Field(value = "name")
    private String name;

    @Field(value = "type")
    private int type;

    @Field(value = "sort")
    private int sort;


}
