package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.SongListSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlSongListSongRepository extends JpaRepository<SongListSong, Integer>, JpaSpecificationExecutor<SongListSong> {


    List<SongListSong> findAllByDeletedAtIsNull();

}
