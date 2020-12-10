package com.bestarmedia.migration.model.mongo.vod;

import com.bestarmedia.migration.model.mongo.Auditing;
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
@Document(value = "vod_song_version_name")
@AllArgsConstructor
@NoArgsConstructor
public class VodVersionsType extends Auditing {

    @Field(value = "code")
    private Integer code;//id

    @Field(value = "name")
    private String name;

    @Field(value = "type")
    private Integer type;

    @Field(value = "sort")
    private Integer sort;


}