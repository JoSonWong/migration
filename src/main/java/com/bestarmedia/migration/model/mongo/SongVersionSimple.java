//package com.bestarmedia.migration.model.mongo;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.util.List;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class SongVersionSimple {
//
//    @Field(value = "code")
//    private Integer code;//id
//
//    @Field(value = "type")
//    private Integer type;//类型  1为视频，2为音画
//
//    @Field(value = "versions_type_code")
//    private Integer versionsTypeCode;//关联版本表（视频-MV，视频-重制，音画-MV ...）
//
//    @Field(value = "versions_hot_sum")
//    private Long versionHotSum;//显示热度
//
//    @Field(value = "recommend")
//    private Integer recommend;//推荐度
//
//    @Field(value = "status")
//    private Integer status;//状态
//
//    @Field(value = "file_simples")
//    private List<FileSimple> fileSimples;//文件
//
//}
