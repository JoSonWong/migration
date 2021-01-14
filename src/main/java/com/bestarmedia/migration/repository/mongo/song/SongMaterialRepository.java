package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongMaterial;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongMaterialRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = songMongoTemplate.remove(query, SongMaterial.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public SongMaterial findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongMaterial.class);
    }

    private SongMaterial insert(SongMaterial songLanguage) {
        return songMongoTemplate.insert(songLanguage);
    }

    public List<SongMaterial> findAll() {
        return songMongoTemplate.findAll(SongMaterial.class);
    }

}
