package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "vod_language")
@Where(clause = "deleted_at is NULL")
public class Language extends MysqlBaseModel {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "language_name")
    private String languageName;

    @Column(name = "sort")
    private int sort;

    @Column(name = "is_show")
    private int isShow;

}
