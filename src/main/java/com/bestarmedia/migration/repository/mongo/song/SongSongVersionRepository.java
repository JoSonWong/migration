package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongSongVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class SongSongVersionRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongSongVersion insert(SongSongVersion vodSongVersion) {
        return songMongoTemplate.insert(vodSongVersion);
    }

    public SongSongVersion replace(SongSongVersion vodSongVersion) {
        songMongoTemplate.remove(new Query(Criteria.where("code").is(vodSongVersion.getCode())), SongSongVersion.class);
        return songMongoTemplate.insert(vodSongVersion);
    }
}
