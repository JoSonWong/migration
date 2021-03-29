package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlEmojiRepository extends JpaRepository<Emoji, Integer>, JpaSpecificationExecutor<Emoji> {

    List<Emoji> findAllByDeletedAtIsNull();

}
