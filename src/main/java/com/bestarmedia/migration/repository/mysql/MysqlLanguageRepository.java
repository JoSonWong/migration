package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MysqlLanguageRepository extends JpaRepository<Language, Integer>, JpaSpecificationExecutor<Language> {

    /**
     * 动态查询.
     */
    Page<Language> findAll(Specification<Language> var1, Pageable pageable);

}
