package com.bestarmedia.migration.model.mongo.ktv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KtvVersionSimple {

    @Field(value = "code")
    private int code;//id

    @Field(value = "type")
    private int type;//类型  1为视频，2为音画

    @Field(value = "versions_type")
    private int versionsType;//关联版本表（视频-MV，视频-重制，音画-MV ...）

    @Field(value = "versions_name")
    private String versionsName;//自定义名称

    @Indexed
    @Field(value = "versions_hot_sum")
    private long versionHotSum;//显示热度

    @Indexed
    @Field(value = "recommend")
    private int recommend;//推荐度

    @Field(value = "status")
    private int status;//状态

    @Field(value = "file")
    private KtvVersionFileSimple file;//文件

}
