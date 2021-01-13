package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("mysqlPartRepository")
public interface MysqlPartRepository extends JpaRepository<Part, Integer>, JpaSpecificationExecutor<Part> {

    List<Part> findAllByDeletedAtIsNullOrderBySortAscIdAsc();
}
