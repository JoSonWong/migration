package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.MusicianSimple;
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

@Repository("mysqlMusicianSimpleRepository")
public interface MysqlMusicianSimpleRepository extends JpaRepository<MusicianSimple, Integer>, JpaSpecificationExecutor<MusicianSimple> {
    /**
     * 动态查询.
     */
    Page<MusicianSimple> findAll(Specification<MusicianSimple> var1, Pageable pageable);

    MusicianSimple findFirstByMusicianName(String name);

}
