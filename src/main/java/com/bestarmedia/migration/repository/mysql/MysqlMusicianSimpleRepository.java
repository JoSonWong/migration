package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.MusicianSimple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MysqlMusicianSimpleRepository extends JpaRepository<MusicianSimple, Integer>, JpaSpecificationExecutor<MusicianSimple> {
    /**
     * 动态查询.
     */
    Page<MusicianSimple> findAll(Specification<MusicianSimple> var1, Pageable pageable);

    MusicianSimple findFirstByMusicianName(String name);

}
