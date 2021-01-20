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
@Document(value = "ktv_album")
@AllArgsConstructor
@NoArgsConstructor
public class KtvAlbum extends Auditing {

    @Field(value = "code")
    private int code;

    @Field(value = "album_name")
    private String albumName;

    @Field(value = "img_file_path")
    private String imgFilePath;

    @Field(value = "remark")
    private String remark;


}
