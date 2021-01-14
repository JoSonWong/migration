package com.bestarmedia.migration.model.mongo.song;

import com.bestarmedia.migration.model.mongo.CodeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(value = "song_versions")
@AllArgsConstructor
@NoArgsConstructor
public class SongSongVersionSimple {

    @Id
    @Field(value = "_id")
    private String _id;

    @Field(value = "code")
    private int code;//id

    @Field(value = "song")
    private CodeName song;//歌曲id+歌名

    @Field(value = "singer")
    private List<CodeName> singer;//歌星

    @Field(value = "litigant")
    private List<CodeName> litigant;//诉讼权利人

    @Field(value = "producer")
    private List<CodeName> producer;//制作权利人

    @Field(value = "publisher")
    private List<CodeName> publisher;//出品权利人

    @Field(value = "versions_hot_sum")
    private long versionHotSum;//显示热度

}
