package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodArea;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VodAreaRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = vodMongoTemplate.remove(query, VodArea.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public VodArea findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodArea.class);
    }

//    public VodArea replace(VodArea songLanguage) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(songLanguage.getCode())), VodArea.class);
//        return vodMongoTemplate.insert(songLanguage);
//    }

    public VodArea insert(VodArea songLanguage) {
        return vodMongoTemplate.insert(songLanguage);
    }
}
