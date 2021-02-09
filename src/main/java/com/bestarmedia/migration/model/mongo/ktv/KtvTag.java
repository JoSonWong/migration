package com.bestarmedia.migration.model.mongo.ktv;


import com.bestarmedia.migration.model.mongo.CodeNameParent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "ktv_tag")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KtvTag extends CodeNameParent {

    @Id
    @Field(value = "_id")
    private String _id;

    @Field(value = "status")
    private int status;

    @Field(value = "sort")
    private int sort;

    @Field(value = "remark")
    private String remark;

    @CreatedDate
    @Field(value = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @Field(value = "updated_at")
    private Date updatedAt;

    @Field(value = "deleted_at")
    private Date deletedAt;

}
