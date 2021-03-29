package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Barrage;
import com.bestarmedia.migration.model.mysql.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlBarrageRepository extends JpaRepository<Barrage, Integer>, JpaSpecificationExecutor<Barrage> {

    List<Barrage> findAllByDeletedAtIsNull();

}
