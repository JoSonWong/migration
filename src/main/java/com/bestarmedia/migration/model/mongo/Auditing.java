package com.bestarmedia.migration.model.mongo;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
public class Auditing {

    @Id
    @Field(value = "_id")
    private String _id;

    @CreatedDate
    @Field(value = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field(value = "updated_at")
    private Date updatedAt;

    @Field(value = "deleted_at")
    private Date deletedAt;
}
