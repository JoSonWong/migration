package com.bestarmedia.migration.model.mongo.ktv;


import com.bestarmedia.migration.model.mongo.Auditing;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.CodeNameParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "ktv_song")
@AllArgsConstructor
@NoArgsConstructor
public class KtvSong extends Auditing {

    @Field(value = "code")
    private int code;//歌曲唯一标识

    @Field(value = "song_initial")
    private String songInitial;//简拼

    @Field(value = "song_name")
    private String songName;//歌名

    @Field(value = "word_count")
    private int wordCount;//字数

    @Field(value = "song_type")
    private CodeNameParent songType;//分类

    @Field(value = "singer")
    private List<CodeName> singer;//歌手

    @Field(value = "lyricist")
    private List<CodeName> lyricist;//词作者

    @Field(value = "composer")
    private List<CodeName> composer;//曲作者

    @Field(value = "language")
    private CodeName language;//语种

    @Field(value = "tag")
    private List<CodeNameParent> tag;//标签

    @Field(value = "hot")
    private long hot;//真实热度

    @Field(value = "hot_sum")
    private long hotSum;//总热度

    @Field(value = "recommend")
    private int recommend;//推荐度

    @Field(value = "status")
    private int status;//上下架状态 下架：0，上架：1

    @Field(value = "versions")
    private List<KtvVersionSimple> versions;

}