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
@Document(value = "ktv_barrage")
@AllArgsConstructor
@NoArgsConstructor
public class KtvBarrage extends Auditing {

    @Field(value = "code")
    private Integer code;

    @Field(value = "content")
    private String content;//歌单名称

    @Field(value = "status")
    private Integer status;//状态 0未发布，1已发布

    @Field(value = "sort")
    private Integer sort;

}
