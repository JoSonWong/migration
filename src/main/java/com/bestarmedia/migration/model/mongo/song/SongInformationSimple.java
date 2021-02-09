package com.bestarmedia.migration.model.mongo.song;


import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.CodeNameParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "song_information")
@AllArgsConstructor
@NoArgsConstructor
public class SongInformationSimple {

    @Id
    @Field(value = "_id")
    private String _id;

    @Field(value = "code")
    private int code;//歌曲唯一标识

    @Field(value = "song_type")
    private CodeNameParent songType;//分类

    @Field(value = "singer")
    private List<CodeName> singer;//歌手

    @Field(value = "lyricist")
    private List<CodeName> lyricist;//词作者

    @Field(value = "composer")
    private List<CodeName> composer;//曲作者

    @Field(value = "hot_sum")
    private long hotSum;//总热度
}
