package com.bestarmedia.migration.repository.mysql;


import com.bestarmedia.migration.model.mysql.SongType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlSongTypeRepository extends JpaRepository<SongType, Integer>, JpaSpecificationExecutor<SongType> {

    List<SongType> findAllByParentIdOrderBySortAscIdAsc(Integer parentId);

    @Override
    <S extends SongType> List<S> saveAll(Iterable<S> iterable);

}
