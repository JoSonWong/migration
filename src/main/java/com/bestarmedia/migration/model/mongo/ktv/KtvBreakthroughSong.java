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
@Document(value = "ktv_breakthrough_song")
@AllArgsConstructor
@NoArgsConstructor
public class KtvBreakthroughSong extends Auditing {

    @Field(value = "code")
    private Integer code;

    @Field(value = "version_code")
    private int versionCode;

    @Field(value = "breakthrough_code")
    private int breakthroughCode;

}
