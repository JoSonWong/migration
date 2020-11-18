package com.bestarmedia.migration.repository.mongo.song;


import com.bestarmedia.migration.model.mongo.song.SongSongType;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongSongTypeRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongSongType findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongSongType.class);
    }


    public long cleanAllData() {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").gte(0));
        DeleteResult result = songMongoTemplate.remove(query, SongSongType.class);
        return result.getDeletedCount();       //返回执行的条
    }


//    public SongSongType replace(SongSongType songType) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(songType.getCode())), SongSongType.class);
//        return songMongoTemplate.insert(songType);
//    }

    public SongSongType insert(SongSongType songType) {
        return songMongoTemplate.insert(songType);
    }

    public List<SongSongType> findAll() {
        return songMongoTemplate.findAll(SongSongType.class);
    }
}
