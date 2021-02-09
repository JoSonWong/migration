//package com.bestarmedia.migration.model.mongo.song;
//
//import com.bestarmedia.migration.model.mongo.CodeName;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class SongType extends CodeName {
//
//    @Field(value = "parent_code")
//    private int parentCode;//id
//
//    @Field(value = "parent_name")
//    private String parentName;//名
//
//
//    public SongType(int code, String name, int parentCode, String parentName) {
//        super(code, name);
//        this.parentCode = parentCode;
//        this.parentName = parentName;
//    }
//}
