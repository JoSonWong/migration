package com.bestarmedia.migration.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KtvUgcOwner {

    @Field(value = "user_id")
    private String userId;//id

    @Field(value = "user_name")
    private String userName;//名

    @Field(value = "user_avatar")
    private String userAvatar;//头像

    @Field(value = "ktv_net_code")
    private String ktvNetCode;//id

    @Field(value = "ktv_name")
    private String ktvName;//名

}
