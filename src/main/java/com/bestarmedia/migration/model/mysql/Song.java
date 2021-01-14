package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "vod_song")
@Where(clause = "deleted_at is NULL")
public class Song extends MysqlBaseModel implements Serializable {

    @Id
    @Column(name = "id")
    private int id;//自增ID

    @Column(name = "s_id")
    private int songId;//歌曲id（理论上与自增id相等）

    @Column(name = "old_sid")
    private int oldSid;//旧的歌曲id

    @Column(name = "song_code")
    private long songCode;//歌曲唯一标识

    @Column(name = "word_count")
    private int wordCount;//歌名字数

    @Column(name = "audio_track")
    private int audioTrack;//原伴唱信息；1一轨原唱，2二轨原唱，3左原唱，4右原唱，5纯欣赏

    @Column(name = "video_type")
    private int videoType;//视频类型

    @OneToOne(targetEntity = VideoType.class)
    @JoinColumn(name = "video_type", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private VideoType videoTypeDetail;

    @Column(name = "video_rhythm")
    private int videoRhythm;//视频节奏

    @Column(name = "music_rhythm")
    private int musicRhythm;//音乐节奏

    @Column(name = "song_type_id")
    private int songTypeId;//歌曲类型

    @OneToOne(targetEntity = SongType.class)
    @JoinColumn(name = "song_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private SongType songType;

    @Column(name = "language_id")
    private int languageId;//语种id

    @OneToOne(targetEntity = Language.class)
    @JoinColumn(name = "language_id", referencedColumnName = "id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Language language;

    @Column(name = "hot")
    private long hot;//大数据热度

    @Column(name = "default_hot")
    private long defaultHot;//默认热度

    @Column(name = "lightness")
    private int lightness;//明暗

    @Column(name = "chroma")
    private int chroma;//色度

    @Column(name = "saturation")
    private int saturation;//饱和度

    @Column(name = "coldwarm")
    private int coldwarm;//冷暖度

    @Column(name = "mildsevere")
    private int mildsevere;//

    @Column(name = "softhard")
    private int softhard;//V6用于推荐度

    @Column(name = "intensity")
    private int intensity;//强弱度

    @Column(name = "brightness")
    private int brightness;//明快度

    @Column(name = "excitability")
    private int excitability;//兴奋度

    @Column(name = "gorgeously")
    private int gorgeously;//华丽度

    @Column(name = "source")
    private int source;//来源：0,批量  1,单曲

    @Column(name = "song_name")
    private String songName;//歌名

    @Column(name = "song_initial")
    private String songInitial;//歌曲拼音首字母

    @Column(name = "singer")
    private String singer;//歌星

    @Column(name = "singer_mid")
    private String singerMid;//歌星id

    @Column(name = "lyricist")
    private String lyricist;//词作者

    @Column(name = "lyricist_mid")
    private String lyricistMid;//词作者id

    @Column(name = "composer")
    private String composer;//曲作者

    @Column(name = "composer_mid")
    private String composerMid;//曲作者id

    @Column(name = "litigant")
    private String litigant;//诉讼权利人

    @Column(name = "litigant_mid")
    private String litigantMid;//诉讼权利人id

    @Column(name = "producer")
    private String producer;//制作权利人

    @Column(name = "producer_mid")
    private String producerMid;//制作权利人id

    @Column(name = "publisher")
    private String publisher;//出品权利人

    @Column(name = "publisher_mid")
    private String publisherMid;//出品权利人id

    @Column(name = "main_lyric")
    private String mainLyric;//主要/精彩 歌词

    @Column(name = "source_file_path")
    private String sourceFilePath;//源文件地址

    @Column(name = "source_file_md5")
    private String sourceFileMd5;//源文件md5

    @Column(name = "source_file_size")
    private long sourceFileSize;//源文件大小

    @Column(name = "source_file_preview")
    private String sourceFilePreview;//源文件视频预览图片

    @Column(name = "media_file_path")
    private String mediaFilePath;//视频文件路径

    @Column(name = "local_media_file_path")
    private String localMediaFilePath;//本地媒体文件路径

    @Column(name = "local_exist_media_file")
    private int localExistMediaFile;//本地是否存在文件

    @Column(name = "media_file_md5")
    private String mediaFileMd5;//文件MD5

    @Column(name = "media_file_size")
    private long mediaFileSize;//文件大小，单位：B

    @Column(name = "lyric_file_path")
    private String lyricFilePath;//歌词文件路径

    @Column(name = "local_lyric_file_path")
    private String localLyricFilePath;//本地歌词文件相对路径

    @Column(name = "lyric_file_md5")
    private String lyricFileMd5;//文件md5

    @Column(name = "lyric_file_size")
    private long lyricFileSize;//文件md5

    @Column(name = "grade_file_path")
    private String scoreStandardFilePath;//得分标准

    @Column(name = "local_grade_file_path")
    private String localScoreStandardFilePath;//本地评分文件相对路径

    @Column(name = "grade_file_md5")
    private String scoreStandardFileMd5;//文件md5

    @Column(name = "grade_file_size")
    private long scoreStandardFileSize;//文件大小，单位：B

    @Column(name = "coord_file_path")
    private String coordinateFilePath;//坐标

    @Column(name = "local_coord_file_path")
    private String localCoordinateFilePath;//坐标

    @Column(name = "coord_file_md5")
    private String coordinateFileMd5;//本地评分基准文件相对路径

    @Column(name = "coord_file_size")
    private long coordinateFileSize;//文件大小，单位：B

    @Column(name = "duration")
    private int duration;//时长，保存到秒

    @Column(name = "resolution_width")
    private int resolutionWidth;//分辨率宽

    @Column(name = "resolution_height")
    private int resolutionHeight;//分辨率高

    @Column(name = "note_one")
    private String noteOne;//备注一

    @Column(name = "note_two")
    private String noteTwo;//备注二

    @Column(name = "note_three")
    private String noteThree;//备注三

    @Column(name = "volume")
    private int volume;//初始音量0~100

    @Column(name = "publish")
    private int publish;//0 未发布 1 发布 2 冷冻

    @Column(name = "ad_id")
    private int adId;//广告id

    @Column(name = "is_red_packet")
    private int isRedPacket;//是否红包歌曲

    @Column(name = "song_source")
    private int songSource;//歌曲来源 0 来源于网络

    @Column(name = "ktv_net_code")
    private String ktvNetCode;//独家的ktv_net_code

    @Column(name = "status")
    private int status;//状态 0下架，1上架

    @Column(name = "vulgar")
    private int vulgar;//低俗

    @Column(name = "illegal")
    private int illegal;//文化部违禁

    @Column(name = "politics")
    private int politics;//政治错误

    @Column(name = "copyright")
    private int copyright;//版权

    @Column(name = "other")
    private int other;//其他的原因类型

    @Column(name = "on_reason")
    private String onReason;//上架原因

    @Column(name = "off_reason")
    private String offReason;//下架原因

    @Column(name = "freeze_resson")
    private String freezeReason;//冷冻原因
}
