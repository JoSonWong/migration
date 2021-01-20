package com.bestarmedia.migration.repository.mongo.ktv;

import com.bestarmedia.migration.model.mongo.ktv.KtvMaterial;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KtvMaterialRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvMaterial.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public KtvMaterial findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvMaterial.class);
    }

    public KtvMaterial insert(KtvMaterial material) {
        return ktvMongoTemplate.insert(material);
    }

    public List<KtvMaterial> findAll() {
        return ktvMongoTemplate.findAll(KtvMaterial.class);
    }

}
