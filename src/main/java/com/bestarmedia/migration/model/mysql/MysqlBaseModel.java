package com.bestarmedia.migration.model.mysql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties
public abstract class MysqlBaseModel {

    @JsonProperty(value = "create_user")
    @Column(name = "create_user")
    private Integer createUser;

    @JsonProperty(value = "update_user")
    @Column(name = "update_user")
    private Integer updateUser;

    @JsonProperty(value = "created_at")
    @CreatedDate
    @Column(name = "created_at")
    private Date createdAt;

    @JsonProperty(value = "updated_at")
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonProperty(value = "deleted_at")
    @Column(name = "deleted_at")
    private Date deletedAt;
}
