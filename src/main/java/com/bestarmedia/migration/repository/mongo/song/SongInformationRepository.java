package com.bestarmedia.migration.repository.mongo.song;


import com.bestarmedia.migration.model.mongo.song.SongInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongInformationRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public List<SongInformation> findByNameAndSinger(String songName, List<String> singers) {
        return songMongoTemplate.find(new Query(Criteria.where("song_name").is(songName).and("singer.name").in(singers)), SongInformation.class);
    }

    public SongInformation insert(SongInformation song) {
        return songMongoTemplate.insert(song);
    }

    public SongInformation replace(SongInformation song) {
        songMongoTemplate.remove(new Query(Criteria.where("code").is(song.getCode())), SongInformation.class);
        return songMongoTemplate.insert(song);
    }
}


