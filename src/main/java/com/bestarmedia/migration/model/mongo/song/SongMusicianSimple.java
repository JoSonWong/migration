package com.bestarmedia.migration.model.mongo.song;

import com.bestarmedia.migration.model.mongo.SearchKeyword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(value = "song_musician")
@AllArgsConstructor
@NoArgsConstructor
public class SongMusicianSimple {
    @Id
    @Field(value = "_id")
    private String _id;

    @Field(value = "code")
    private Integer code;//ID

    @Field(value = "musician_name")
    private String musicianName;//音乐人名

    @Field(value = "musician_type")
    private List<Integer> musicianType;//音乐人类型  1歌手   2曲   3词

    @Field(value = "img_file_path")
    private String imgFilePath;//封面

    @Field(value = "mold")
    private Integer mold;//(1为已入库0为未入库)

    @Field(value = "search_keywords")
    private List<SearchKeyword> searchKeywords;//搜索关键字
}
