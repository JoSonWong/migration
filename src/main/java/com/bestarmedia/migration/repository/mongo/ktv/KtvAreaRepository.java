package com.bestarmedia.migration.repository.mongo.ktv;

import com.bestarmedia.migration.model.mongo.ktv.KtvArea;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class KtvAreaRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = ktvMongoTemplate.remove(query, KtvArea.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public KtvArea findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvArea.class);
    }

    public KtvArea insert(KtvArea songLanguage) {
        return ktvMongoTemplate.insert(songLanguage);
    }
}
