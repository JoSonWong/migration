package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "vod_song")
@Where(clause = "deleted_at is NULL")
public class SongSimple implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;//自增ID

    @Column(name = "s_id")
    private Integer songId;//歌曲id（理论上与自增id相等）

    @Column(name = "song_name")
    private String songName;//歌名

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

    @Column(name = "lyric_file_path")
    private String lyricFilePath;//歌词文件路径

    @Column(name = "local_lyric_file_path")
    private String localLyricFilePath;//本地歌词文件相对路径

    @Column(name = "lyric_file_md5")
    private String lyricFileMd5;//文件md5


    @Column(name = "video_type")
    private Integer videoType;//视频类型

}
