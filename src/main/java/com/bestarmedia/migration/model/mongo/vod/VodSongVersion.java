package com.bestarmedia.migration.model.mongo.vod;

import com.bestarmedia.migration.model.mongo.Auditing;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.VideoFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(value = "vod_song_version")
@AllArgsConstructor
@NoArgsConstructor
public class VodSongVersion extends Auditing {

    @Field(value = "code")
    private int code;//id

    @Field(value = "song")
    private CodeName song;//歌曲id+歌名

    @Field(value = "singer")
    private List<CodeName> singer;//歌星

    @Field(value = "song_code_old")
    public int songCodeOld;

    @Field(value = "type")
    private int type;//类型  1为视频，2为音画

    @Field(value = "versions_type")
    private int versionsTypeCode;//关联版本表（视频-MV，视频-重制，音画-MV ...）

    @Field(value = "source")
    private String source;//来源

    @Field(value = "album")
    private List<CodeName> album;//专辑

    @Field(value = "increase_hot")
    private long increaseHot;//热度增量

    @Field(value = "versions_hot")
    private long versionHot;//真实热度

    @Field(value = "versions_hot_sum")
    private long versionHotSum;//显示热度

    @Field(value = "recommend")
    private int recommend;//推荐度

    @Field(value = "issue_time")
    private Date issueTime;//发行日期

    @Field(value = "status")
    private int status;//状态

    @Field(value = "file")
    private List<VideoFile> videoFileList;//文件

}
