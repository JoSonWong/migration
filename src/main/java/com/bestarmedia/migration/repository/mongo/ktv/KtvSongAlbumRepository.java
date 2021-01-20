package com.bestarmedia.migration.repository.mongo.ktv;

import com.bestarmedia.migration.model.mongo.ktv.KtvAlbum;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KtvSongAlbumRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvAlbum.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public KtvAlbum findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvAlbum.class);
    }

    public KtvAlbum findByName(String name) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("albumName").is(name)), KtvAlbum.class);
    }

    public KtvAlbum insert(KtvAlbum album) {
        return ktvMongoTemplate.insert(album);
    }

    public List<KtvAlbum> findAll() {
        return ktvMongoTemplate.findAll(KtvAlbum.class);
    }

}
