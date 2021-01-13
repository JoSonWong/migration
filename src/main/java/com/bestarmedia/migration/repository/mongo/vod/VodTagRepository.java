package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodTag;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class VodTagRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = vodMongoTemplate.remove(query, VodTag.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public VodTag findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodTag.class);
    }

    public VodTag findByName(String name) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("tagName").is(name)), VodTag.class);
    }

    public VodTag insert(String tagName) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code"));
        VodTag songTag = vodMongoTemplate.findOne(query, VodTag.class);
        int code = 1;
        if (songTag != null) {
            code = songTag.getCode() + 1;
        }
        VodTag insert = new VodTag(code, tagName, 1, 1, 0, "MySQL数据导入");
        insert.setCreatedAt(new Date());
        insert.setUpdatedAt(new Date());
        return vodMongoTemplate.insert(insert);
    }

    public VodTag insert(VodTag songTag) {
        return vodMongoTemplate.insert(songTag);
    }

    public List<VodTag> findAll() {
        return vodMongoTemplate.findAll(VodTag.class);
    }

}
