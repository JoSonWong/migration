package com.bestarmedia.migration.repository.mongo.song;


import com.bestarmedia.migration.model.mongo.song.SongVersionsType;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongSongVersionTypeRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongVersionsType findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongVersionsType.class);
    }

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = songMongoTemplate.remove(query, SongVersionsType.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public SongVersionsType insert(SongVersionsType songType) {
        return songMongoTemplate.insert(songType);
    }

    public List<SongVersionsType> findAll() {
        return songMongoTemplate.findAll(SongVersionsType.class);
    }
}
