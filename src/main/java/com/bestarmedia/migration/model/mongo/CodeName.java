package com.bestarmedia.migration.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeName {

    @Field(value = "code")
    private Integer code;//id

    @Field(value = "name")
    private String name;//名

}
