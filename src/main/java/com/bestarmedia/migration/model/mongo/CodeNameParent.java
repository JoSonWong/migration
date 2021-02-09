package com.bestarmedia.migration.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CodeNameParent extends CodeName {

    @Field(value = "parent_code")
    private Integer parentCode;

    @Field(value = "parent_name")
    private String parentName;//名
}
