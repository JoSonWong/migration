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
@Document(value = "ktv_emoji")
@AllArgsConstructor
@NoArgsConstructor
public class KtvEmoji extends Auditing {

    @Field(value = "code")
    private Integer code;//id

    @Field(value = "name")
    private String name;

    @Field(value = "emoji_package_code")
    private Integer emojiPackageCode;

    @Field(value = "thumbnail_image")
    private String thumbnailImage;

    @Field(value = "style_image")
    private String styleImage;

    @Field(value = "sort")
    private Integer sort;

}
