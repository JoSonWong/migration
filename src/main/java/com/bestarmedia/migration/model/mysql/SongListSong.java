package com.bestarmedia.migration.model.mysql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Table(name = "vod_menu_song_v6")
@Where(clause = "deleted_at is NULL")
public class SongListSong {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "s_id")
    private int songId;

    @Column(name = "menu_id")
    private int songListId;

    @Column(name = "sort")
    private int sort;

    @Column(name = "deleted_at")
    private Date deletedAt;
}
