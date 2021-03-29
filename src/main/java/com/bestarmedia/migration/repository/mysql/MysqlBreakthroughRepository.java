package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Breakthrough;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlBreakthroughRepository extends JpaRepository<Breakthrough, Integer>, JpaSpecificationExecutor<Breakthrough> {

    List<Breakthrough> findAllByDeletedAtIsNull();

}
