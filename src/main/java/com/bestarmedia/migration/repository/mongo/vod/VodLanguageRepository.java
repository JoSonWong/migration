package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodLanguage;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VodLanguageRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = vodMongoTemplate.remove(query, VodLanguage.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public VodLanguage findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodLanguage.class);
    }

//    public VodLanguage replace(VodLanguage VodLanguage) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(VodLanguage.getCode())), VodLanguage.class);
//        return vodMongoTemplate.insert(VodLanguage);
//    }

    public VodLanguage insert(VodLanguage VodLanguage) {
        return vodMongoTemplate.insert(VodLanguage);
    }
}
