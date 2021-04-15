package com.bestarmedia.migration.model.mongo.ktv;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
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
public class KtvTag {

    @Id
    @Field(value = "_id")
    private String _id;

    @Field(value = "code")
    private Integer code;

    @Field(value = "tag_name")
    private String tagName;//标签名称


    @Field(value = "parent_code")
    private Integer parentCode;//组别

    @Field(value = "parent_name")
    private String parentName;//组别

    @Field(value = "sort")
    private Integer sort;//排序

    @Field(value = "status")
    private Integer status;//状态 下架：0，上架：1

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
