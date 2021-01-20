package com.bestarmedia.migration.model.mongo.ktv;


import com.bestarmedia.migration.model.mongo.Auditing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "ktv_tag")
@AllArgsConstructor
@NoArgsConstructor
public class KtvTag extends Auditing {

    @Field(value = "code")
    private int code;

    @Field(value = "tag_name")
    private String tagName;

    @Field(value = "status")
    private int status;

    @Field(value = "sort")
    private int sort;

    @Field(value = "parent_code")
    private int parentCode;

    @Field(value = "remark")
    private String remark;


}
