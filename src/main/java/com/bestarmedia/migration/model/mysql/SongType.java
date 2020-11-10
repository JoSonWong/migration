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
@Table(name = "vod_song_type")
@Where(clause = "deleted_at is NULL")
public class SongType extends MysqlBaseModel {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "p_id")
    private Integer parentId;

    @Column(name = "name")
    private String name;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "light_mode")
    private Integer lightMode;

    @Column(name = "is_show")
    private Integer isShow;
}
