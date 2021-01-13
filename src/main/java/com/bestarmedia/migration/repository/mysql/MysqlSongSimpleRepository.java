package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.SongSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository(value = "mysqlSongSimpleRepository")
public interface MysqlSongSimpleRepository extends JpaRepository<SongSimple, Integer>, JpaSpecificationExecutor<SongSimple> {

    @Override
    <S extends SongSimple> List<S> saveAll(Iterable<S> iterable);

    /**
     * 动态查询.
     */
    Page<SongSimple> findAll(Specification<SongSimple> var1, Pageable pageable);

    @Override
    List<SongSimple> findAll(Specification<SongSimple> specification, Sort sort);


    SongSimple findSongBySongId(int sId);


    List<SongSimple> findAllBySongName(String songName);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set local_lyric_file_path=:local_lyric_file_path ,lyric_file_md5=:lyric_file_md5  where s_id=:id", nativeQuery = true)
    int updateReleaseTimeAndTag(@Param(value = "local_lyric_file_path") String local_lyric_file_path, @Param(value = "lyric_file_md5") String lyric_file_md5, @Param(value = "id") int id);
}
