package com.bestarmedia.migration.model.mongo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialVideoDTO {

    @Field(value = "video_code")
    private int videoCode;

    @Field(value = "format_code")
    private int formatCode;

    @Field(value = "format_name")
    private String formatName;

    @Field(value = "file_path")
    private String filePath;
}
