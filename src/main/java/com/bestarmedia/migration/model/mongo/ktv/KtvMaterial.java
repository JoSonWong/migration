package com.bestarmedia.migration.model.mongo.ktv;

import com.bestarmedia.migration.model.mongo.Auditing;
import com.bestarmedia.migration.model.mongo.CodeNameParent;
import com.bestarmedia.migration.model.mongo.MaterialVideoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(value = "ktv_material")
@AllArgsConstructor
@NoArgsConstructor
public class KtvMaterial extends Auditing {

    @Field(value = "code")
    private Integer code;//id

    @Field(value = "material_name")
    private String materialName;

    @Field(value = "musician_code")
    private Integer musicianCode;

    @Field(value = "status")
    private Integer status;//状态

    @Field(value = "type")
    private Integer type;//1为图片 2为视频

    @Field(value = "sort")
    private Integer sort;

    @Field(value = "img_file_path")
    private String imgFilePath;

    @Field(value = "video")
    private MaterialVideoDTO video;

    @Field(value = "tag")
    private List<CodeNameParent> tag;

    @Field(value = "remark")
    private String remark;

    @Field(value = "song_type")
    private List<CodeNameParent> songType;

}
