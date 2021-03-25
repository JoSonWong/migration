package com.bestarmedia.migration.model.mongo.ktv;

import com.bestarmedia.migration.model.mongo.Auditing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(value = "ktv_song_list_song")
@AllArgsConstructor
@NoArgsConstructor
public class KtvSongListSong extends Auditing {

    @Field(value = "code")
    private Integer code;//id

    @Field(value = "version_code")
    private Integer versionCode;//歌曲id

    @Field(value = "song_list_code")
    private Integer songListCode;//歌单id

    @Field(value = "sort")
    private Integer sort;

}
