package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.EmojiDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MysqlEmojiDetailRepository extends JpaRepository<EmojiDetail, Integer>, JpaSpecificationExecutor<EmojiDetail> {


    List<EmojiDetail> findAllByDeletedAtIsNull();

}
