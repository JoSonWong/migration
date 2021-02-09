package com.bestarmedia.migration.model.mongo.song;


import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.CodeNameParent;
import com.bestarmedia.migration.model.mongo.KtvUgcOwner;
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
@Document(value = "song_ugc")
@AllArgsConstructor
@NoArgsConstructor
public class SongUgc extends SongAuditing {

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

    @Field(value = "language")
    private CodeName language;//语种

    @Field(value = "status")
    private int status;//上下架状态 下架：0，上架：1

    @Field(value = "ugc_owner")
    private KtvUgcOwner ugcOwner;//原创者信息

    @Field(value = "file_path")
    private String filePath;//视频文件地址或

    @Field(value = "resolution_width")
    private int resolutionWidth;//分辨率 宽

    @Field(value = "resolution_height")
    private int resolutionHeight;//分辨率 高

    @Field(value = "audio_track")
    private Integer audioTrack;//原伴唱

    @Field(value = "volume")
    private Integer volume;//均衡音量

    @Field(value = "remark")
    private String remark;//备注

}
