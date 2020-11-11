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
    private Integer code;//id

    @Field(value = "song_code")
    private Integer songCode;//歌曲id

    @Field(value = "song_code_old")
    public Integer songCodeOld;

    @Field(value = "type")
    private Integer type;//类型  1为视频，2为音画

    @Field(value = "versions_type")
    private Integer versionsType;//版本类型 (原mysql视频类型id)

    @Field(value = "versions_name")
    private String versionsName;//版本标签（原：如MV、现场 等)

    @Field(value = "source")
    private String source;//来源

    @Field(value = "album")
    private CodeName album;//专辑

//    @Field(value = "litigant")
//    private List<CodeName> litigant;//诉讼权利人
//
//    @Field(value = "producer")
//    private List<CodeName> producer;//制作权利人
//
//    @Field(value = "publisher")
//    private List<CodeName> publisher;//出品权利人

    @Field(value = "increase_hot")
    private Long increaseHot;//热度增量

    @Field(value = "versions_hot")
    private Long versionHot;//真实热度

    @Field(value = "versions_hot_sum")
    private Long versionHotSum;//显示热度

    @Field(value = "recommend")
    private Integer recommend;//推荐度

    @Field(value = "issue_time")
    private Date issueTime;//发行日期

    @Field(value = "status")
    private Integer status;//状态

    @Field(value = "file")
    private List<VideoFile> videoFileList;//文件

}
