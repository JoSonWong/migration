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
@Table(name = "vod_emoji")
@Where(clause = "deleted_at is NULL")
public class Emoji {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "cover_img")
    private String coverImg;

    @Column(name = "status")
    private int status;

    @Column(name = "sort")
    private int sort;

    @Column(name = "deleted_at")
    private Date deletedAt;
}
