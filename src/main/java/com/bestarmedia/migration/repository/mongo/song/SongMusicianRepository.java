package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongMusician;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SongMusicianRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongMusician findSingerByName(String name) {
        return songMongoTemplate.findOne(new Query(Criteria.where("musician_name").is(name)), SongMusician.class);
    }


    public SongMusician replace(SongMusician musician) {
        songMongoTemplate.remove(new Query(Criteria.where("code").is(musician.getCode())), SongMusician.class);
        return songMongoTemplate.insert(musician);
    }

    public SongMusician insert(SongMusician singer) {
        return songMongoTemplate.insert(singer);
    }

}
