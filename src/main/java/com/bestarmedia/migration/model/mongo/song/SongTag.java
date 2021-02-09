package com.bestarmedia.migration.model.mongo.song;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "song_tag")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SongTag extends SongAuditing {

    @Field(value = "code")
    private int code;

    @Field(value = "tag_name")
    private String tagName;

    @Field(value = "parent_code")
    private int parentCode;

    @Field(value = "status")
    private int status;

    @Field(value = "sort")
    private int sort;

    @Field(value = "remark")
    private String remark;

}
