package com.bestarmedia.migration.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagSimple {

    @Field(value = "tag_code")
    private int tagCode;//id

    @Field(value = "tag_name")
    private String tagName;//名

    @Field(value = "parent_code")
    private Integer parentCode;

    @Field(value = "parent_name")
    private String parentName;//名
}
