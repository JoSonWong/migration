package com.bestarmedia.migration.model.mongo.song;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "song_tag")
@AllArgsConstructor
@NoArgsConstructor
public class SongTag extends SongAuditing {

    @Field(value = "code")
    private Integer code;

    @Field(value = "tag_name")
    private String tagName;

    @Field(value = "status")
    private Integer status;

    @Field(value = "sort")
    private Integer sort;

    @Field(value = "parent_code")
    private Integer parentCode;

    @Field(value = "remark")
    private String remark;


}
