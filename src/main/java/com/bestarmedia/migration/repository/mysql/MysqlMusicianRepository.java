package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Musician;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MysqlMusicianRepository extends JpaRepository<Musician, Integer>, JpaSpecificationExecutor<Musician> {
    /**
     * 动态查询.
     */
    Page<Musician> findAll(Specification<Musician> var1, Pageable pageable);

    Musician findMusicianByMusicianIdAndStatus(int mid, int status);

    Musician findMusicianByMusicianId(int mid);

    Musician findMusicianByMusicianName(String name);

    Musician findFirstByMusicianName(String name);
    /**
     * 更新热度
     *
     * @param hot 热度
     * @param id  id
     * @return 影响条数
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_musician set hot=:hot where m_id=:id", nativeQuery = true)
    int updateHot(@Param(value = "hot") int hot, @Param(value = "id") int id);


    /**
     * 更新状态
     *
     * @param status 状态
     * @param id     id
     * @return 影响条数
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_musician set status=:status where m_id=:id", nativeQuery = true)
    int updateStatus(@Param(value = "status") int status, @Param(value = "id") int id);


    @Query(value = "select * from vod_musician as s where  s.deleted_at is NULL ORDER BY s.musician_name ASC limit :startIndex, :pageSize", nativeQuery = true)
    List<Musician> getMusicianSameName(@Param(value = "startIndex") int startIndex, @Param(value = "pageSize") int pageSize);


    @Query(value = "select count(*) from vod_musician as s where s.deleted_at is NULL ORDER BY s.musician_name ASC  ", nativeQuery = true)
    Integer getMusicianSameNameCount();
}
