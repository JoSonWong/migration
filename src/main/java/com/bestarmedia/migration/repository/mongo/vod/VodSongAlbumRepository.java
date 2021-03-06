package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodAlbum;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VodSongAlbumRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = vodMongoTemplate.remove(query, VodAlbum.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public VodAlbum findByCode(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodAlbum.class);
    }

    public VodAlbum findByName(String name) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("albumName").is(name)), VodAlbum.class);
    }

    public VodAlbum insert(VodAlbum album) {
        return vodMongoTemplate.insert(album);
    }

    public List<VodAlbum> findAll() {
        return vodMongoTemplate.findAll(VodAlbum.class);
    }

}
