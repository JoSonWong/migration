package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodMaterial;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VodMaterialRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = vodMongoTemplate.remove(query, VodMaterial.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public VodMaterial findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodMaterial.class);
    }

    public VodMaterial insert(VodMaterial material) {
        return vodMongoTemplate.insert(material);
    }

    public List<VodMaterial> findAll() {
        return vodMongoTemplate.findAll(VodMaterial.class);
    }

}
