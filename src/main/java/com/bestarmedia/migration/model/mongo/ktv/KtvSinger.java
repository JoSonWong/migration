package com.bestarmedia.migration.model.mongo.ktv;

import com.bestarmedia.migration.model.mongo.Auditing;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.SearchKeyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@Document(value = "ktv_musician")
@AllArgsConstructor
@NoArgsConstructor
public class KtvSinger extends Auditing {

    @Field(value = "code")
    private int code;//ID

    @Field(value = "musician_type")
    private List<Integer> musicianType;//音乐人类型  1歌手   2曲   3词

    @Field(value = "simplicity")
    private String simplicity;//名字简写

    @Field(value = "musician_name")
    private String musicianName;//音乐人名

    @Field(value = "word_count")
    private int wordCount;//字数

    @Field(value = "hot")
    private long hot;//热度

    @Field(value = "hot_sum")
    private long hotSum;//热度

    @Field(value = "sex")
    private int sex;//性别 1 男 2 女  3混合组合

    @Field(value = "birthday")
    private Date birthday;//生日

    @Field(value = "role")
    private int role;//身份 0 歌手 1 组合 2 公司

    @Field(value = "part")
    private CodeName part;//地区

    @Field(value = "img_file_path")
    private String imgFilePath;//封面

    @Field(value = "status")
    private int status;//是否上架

    @Field(value = "alias")
    private List<String> alias;//别名

    @Field(value = "search_keywords")
    private List<SearchKeyword> searchKeywords;//搜索关键字

//    @Field(value = "file_formats")
//    private List<MusicianFileFormat> musicianFileFormats;//名下歌曲版本统计

}
