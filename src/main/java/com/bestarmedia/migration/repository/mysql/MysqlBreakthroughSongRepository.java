package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.BreakthroughSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlBreakthroughSongRepository extends JpaRepository<BreakthroughSong, Integer>, JpaSpecificationExecutor<BreakthroughSong> {


    List<BreakthroughSong> findAllByDeletedAtIsNull();

}
