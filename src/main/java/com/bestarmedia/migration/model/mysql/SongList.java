package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "vod_menu_v6")
@Where(clause = "deleted_at is NULL")
public class SongList {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "status")
    private int status;

    @Column(name = "hot")
    private Long hot;

    @Column(name = "deleted_at")
    private Date deletedAt;
}
