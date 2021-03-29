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
@Table(name = "vod_breakthrough_song")
@Where(clause = "deleted_at is NULL")
public class BreakthroughSong {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "s_id")
    private int songId;

    @Column(name = "b_id")
    private int breakthroughId;


    @Column(name = "deleted_at")
    private Date deletedAt;
}
