package com.bestarmedia.migration.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchKeyword {

    @Field(value = "type")
    private int type;//0本身信息，1关联音乐人信息

    @Field(value = "code")
    private int code;//编号，如音乐人编号

    @Field(value = "keywords")
    private List<String> keywords;//搜索关键字，用于存储：歌星名、歌星简拼、歌星曾用名 等需要搜索的字段


}
