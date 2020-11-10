package com.bestarmedia.migration.model.mongo.song;

import com.bestarmedia.migration.model.mongo.Auditing;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class SongAuditing extends Auditing {

    @Field(value = "create_user")
    private Integer createUser;

    @Field(value = "update_user")
    private Integer updateUser;
}
