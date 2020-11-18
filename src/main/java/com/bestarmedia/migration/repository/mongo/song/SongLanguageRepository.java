package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongLanguage;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongLanguageRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").gte(0));
        DeleteResult result = songMongoTemplate.remove(query, SongLanguage.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public SongLanguage findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongLanguage.class);
    }

//    public SongLanguage replace(SongLanguage songLanguage) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(songLanguage.getCode())), SongLanguage.class);
//        return songMongoTemplate.insert(songLanguage);
//    }

    public SongLanguage insert(SongLanguage songLanguage) {
        return songMongoTemplate.insert(songLanguage);
    }

    public List<SongLanguage> findAll() {
        return songMongoTemplate.findAll(SongLanguage.class);
    }

}
