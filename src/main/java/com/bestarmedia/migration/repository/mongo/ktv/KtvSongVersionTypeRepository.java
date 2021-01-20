package com.bestarmedia.migration.repository.mongo.ktv;


import com.bestarmedia.migration.model.mongo.ktv.KtvVersionName;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class KtvSongVersionTypeRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public KtvVersionName findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvVersionName.class);
    }

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvVersionName.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public KtvVersionName insert(KtvVersionName songType) {
        return ktvMongoTemplate.insert(songType);
    }

}
