package com.bestarmedia.migration.repository.mongo.ktv;

import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.ktv.KtvSongVersion;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class KtvSongVersionRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public KtvSongVersion update(Integer code, List<CodeName> litigant, List<CodeName> producer, List<CodeName> publisher, Date issueTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("litigant", litigant);
        update.set("producer", producer);
        update.set("publisher", publisher);
        update.set("issue_time", issueTime);
        ktvMongoTemplate.updateFirst(query, update, KtvSongVersion.class);
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvSongVersion.class);
    }


    public KtvSongVersion insert(KtvSongVersion vodSongVersion) {
        return ktvMongoTemplate.insert(vodSongVersion);
    }


    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvSongVersion.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public long updateLyricFile(String fileRemark, String lyricFile) {
        return updateFilePath(fileRemark, "file.lyric_file_path", lyricFile);
    }

    public long updateFilePath(String fileRemark, String field, String filePath) {
        Query query = new Query();
        query.addCriteria(Criteria.where("file.remark").is(fileRemark));
        Update update = new Update();
        update.set(field, filePath);
        UpdateResult updateResult = ktvMongoTemplate.updateFirst(query, update, KtvSongVersion.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

}
