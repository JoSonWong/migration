package com.bestarmedia.migration.model.mongo.vod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VodSongVideoFile {

    @Field(value = "code")
    private String code;//文件id

    @Field(value = "file_name")
    private String fileName;//文件名

    @Field(value = "file_path")
    private String filePath;//视频文件地址

    @Field(value = "format_name")
    private String formatName;//格式类型名

    @Field(value = "video_type")
    private String videoType;//视频类型

    @Field(value = "audio_track")
    private Integer audioTrack;//原伴唱

    @Field(value = "resolution_width")
    private Integer resolutionWidth;//分辨率 宽

    @Field(value = "resolution_height")
    private Integer resolutionHeight;//分辨率 高

    @Field(value = "volume")
    private Integer volume;//均衡音量

    @Field(value = "score_file_path")
    private String scoreFilePath;//评分文件地址

    @Field(value = "lyric_file_path")
    private String lyricFilePath;//歌词文件地址

    @Field(value = "hot")
    private Integer hot;//点播量

    @Field(value = "status")
    private Integer status;//状态

    @Field(value = "remark")
    private String remark;//备注

}
