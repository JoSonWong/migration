package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongArea;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongAreaRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").gte(0));
        DeleteResult result = songMongoTemplate.remove(query, SongArea.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public SongArea findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongArea.class);
    }

//    public SongArea replace(SongArea songLanguage) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(songLanguage.getCode())), SongArea.class);
//        return songMongoTemplate.insert(songLanguage);
//    }

    public SongArea insert(SongArea songLanguage) {
        return songMongoTemplate.insert(songLanguage);
    }

    public List<SongArea> findAll() {
        return songMongoTemplate.findAll(SongArea.class);
    }

}
