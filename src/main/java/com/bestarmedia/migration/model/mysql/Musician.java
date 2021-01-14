package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "vod_musician")
@Where(clause = "deleted_at is NULL")
public class Musician extends MysqlBaseModel implements Serializable {

    @Id
    @Column(name = "id")
    private int id;//自增ID

    @Column(name = "m_id")
    private int musicianId;//音乐人id

    @Column(name = "old_mid")
    private int oldMid;//旧的音乐人id

    @Column(name = "musician_code")
    private long musicianCode;//唯一编码

    @Column(name = "simp_name")
    private String musicianInitial;//歌曲拼音首字母

    @Column(name = "musician_name")
    private String musicianName;//音乐人名

    @Column(name = "word_count")
    private int wordCount;//字数

    @Column(name = "hot")
    private int hot;//热度

    @Column(name = "sex")
    private int sex;//性别 1 男 2 女

    @Column(name = "birthday")
    private Date birthday;//生日

    @Column(name = "role")
    private int role;//身份 0 歌手 1 组合 2 公司

    @Column(name = "band")
    private int band;//组合 0 非组合 1 男组合 2 女组合 3组合

    @Column(name = "part")
    private int part;//地区

    @Column(name = "img_file_name")
    private String imgFileName;//图片名

    @Column(name = "img_file_path")
    private String imgFilePath;//图片路径

    @Column(name = "local_img_file_path")
    private String localImgFilePath;//图片路径相对路径

    @Column(name = "rela_ids")
    private String relaIds;//关联组合id集合

    @Column(name = "is_singer")
    private int isSinger;//是否为歌手

    @Column(name = "is_lyricist")
    private int isLyricist;//是否为词作者

    @Column(name = "is_composer")
    private int isComposer;//是否为曲作者

    @Column(name = "is_litigant")
    private int isLitigant;//是否为诉讼权利人

    @Column(name = "is_producer")
    private int isProducer;//是否为制作权利人

    @Column(name = "is_publisher")
    private int isPublisher;//是否为制作权利人

    @Column(name = "off_singer")
    private int offSinger;//下架歌手

    @Column(name = "off_lyricist")
    private int offLyricist;//下架词作者

    @Column(name = "off_composer")
    private int offComposer;//下架曲作者

    @Column(name = "off_litigant")
    private int offLitigant;//下架诉讼权利人

    @Column(name = "off_producer")
    private int offProducer;//下架制作权利人

    @Column(name = "off_publisher")
    private int offPublisher;//下架出品权利人

    @Column(name = "simp_byname_one")
    private String simpBynameOne;//别名简写

    @Column(name = "byname_one")
    private String bynameOne;//别名

    @Column(name = "one_word_count")
    private int oneWordCount;//名字总数

    @Column(name = "simp_byname_two")
    private String simpBynameTwo;//别名简写2

    @Column(name = "byname_two")
    private String bynameTwo;//别名2

    @Column(name = "two_word_count")
    private int twoWordCount;//名字总数2

    @Column(name = "simp_byname_three")
    private String simpBynameThree;//别名简写3

    @Column(name = "byname_three")
    private String bynameThree;//别名3

    @Column(name = "three_word_count")
    private int threeWordCount;//名字总数4

    @Column(name = "simp_byname_four")
    private String simpBynameFour;//别名简写4

    @Column(name = "byname_four")
    private String bynameFour;//别名4

    @Column(name = "four_word_count")
    private int fourWordCount;//名字总数4

    @Column(name = "tag_ids")
    private String tagIds;//标签id集合

    @Column(name = "tag_name")
    private String tagName;//标签集合

    @Column(name = "status")
    private int status;//状态 1 正常 0 下架

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

    @Column(name = "off_user")
    private int offUser;//下架人
}
