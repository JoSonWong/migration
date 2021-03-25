package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.SongList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlSongListRepository extends JpaRepository<SongList, Integer>, JpaSpecificationExecutor<SongList> {


    List<SongList> findAllByDeletedAtIsNull();

}
