package com.bestarmedia.migration.repository.mongo.ktv;


import com.bestarmedia.migration.model.mongo.ktv.KtvSongType;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class KtvSongTypeRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;


    public KtvSongType findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvSongType.class);
    }

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvSongType.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public KtvSongType insert(KtvSongType songType) {
        return ktvMongoTemplate.insert(songType);
    }

}
