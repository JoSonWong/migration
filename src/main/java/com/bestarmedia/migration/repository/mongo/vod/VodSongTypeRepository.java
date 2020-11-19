package com.bestarmedia.migration.repository.mongo.vod;


import com.bestarmedia.migration.model.mongo.vod.VodSongType;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VodSongTypeRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;


    public VodSongType findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodSongType.class);
    }


    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = vodMongoTemplate.remove(query, VodSongType.class);
        return result.getDeletedCount();       //返回执行的条
    }


//    public VodSongType replace(VodSongType songType) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(songType.getCode())), VodSongType.class);
//        return vodMongoTemplate.insert(songType);
//    }

    public VodSongType insert(VodSongType songType) {
        return vodMongoTemplate.insert(songType);
    }

}
