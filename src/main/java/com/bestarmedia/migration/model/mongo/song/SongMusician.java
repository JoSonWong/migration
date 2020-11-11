package com.bestarmedia.migration.model.mongo.song;

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
@Document(value = "song_musician")
@AllArgsConstructor
@NoArgsConstructor
public class SongMusician extends SongAuditing {

    @Field(value = "code")
    private Integer code;//ID

    @Field(value = "musician_name")
    private String musicianName;//音乐人名

    @Field(value = "musician_type")
    private List<Integer> musicianType;//音乐人类型  1歌手   2曲   3词

    @Field(value = "simplicity")
    private String simplicity;//名字简写

    @Field(value = "word_count")
    private Integer wordCount;//字数

    @Field(value = "hot")
    private Long hot;//热度

    @Field(value = "hot_sum")
    private Long hotSum;//热度

    @Field(value = "sex")
    private Integer sex;//性别 1 男 2 女  3混合组合

    @Field(value = "birthday")
    private Date birthday;//生日

    @Field(value = "role")
    private Integer role;//身份：0歌手 1组合 2公司

    @Field(value = "part")
    private CodeName part;//地区

    @Field(value = "img_file_path")
    private String imgFilePath;//封面

    @Field(value = "status")
    private Integer status;//是否上架

    @Field(value = "alias")
    private List<String> alias;//别名

    @Field(value = "mold")
    private Integer mold;//(1为已入库0为未入库)

    @Field(value = "remark")
    private String remark;//备注

    @Field(value = "search_keywords")
    private List<SearchKeyword> searchKeywords;//搜索关键字
}
