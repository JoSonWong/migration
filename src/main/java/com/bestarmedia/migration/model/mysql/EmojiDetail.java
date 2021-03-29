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
@Table(name = "vod_emoji_detail")
@Where(clause = "deleted_at is NULL")
public class EmojiDetail {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "e_id")
    private int emojiId;

    @Column(name = "name")
    private String name;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "deleted_at")
    private Date deletedAt;
}
