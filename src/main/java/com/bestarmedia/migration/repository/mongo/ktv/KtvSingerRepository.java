package com.bestarmedia.migration.repository.mongo.ktv;

import com.bestarmedia.migration.model.mongo.ktv.KtvSinger;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class KtvSingerRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public KtvSinger insert(KtvSinger singer) {
        return ktvMongoTemplate.insert(singer);
    }

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvSinger.class);
        return result.getDeletedCount();       //返回执行的条
    }

}
