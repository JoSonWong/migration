package com.bestarmedia.migration.model.mongo.ktv;

import com.bestarmedia.migration.model.mongo.Auditing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(value = "ktv_version_name")
@AllArgsConstructor
@NoArgsConstructor
public class KtvVersionName extends Auditing {

    @Field(value = "code")
    private int code;//id

    @Field(value = "name")
    private String name;

    @Field(value = "type")
    private int type;

    @Field(value = "sort")
    private int sort;

}
