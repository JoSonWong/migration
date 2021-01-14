package com.bestarmedia.migration.model.mysql;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "vod_musician")
@Where(clause = "deleted_at is NULL")
public class MusicianSimple implements Serializable {

    @Id
    @Column(name = "id")
    private int id;//自增ID

    @Column(name = "m_id")
    private int musicianId;//音乐人id

    @Column(name = "musician_name")
    private String musicianName;//音乐人名

}
