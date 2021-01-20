package com.bestarmedia.migration.repository.mongo.ktv;


import com.bestarmedia.migration.model.mongo.ktv.KtvSong;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class KtvSongRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;


    public KtvSong insert(KtvSong song) {
        return ktvMongoTemplate.insert(song);
    }

    public KtvSong update(KtvSong song) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(song.getCode()));
        Update update = new Update();
        update.set("song_name", song.getSongName());
        update.set("singer", song.getSinger());
        update.set("lyricist", song.getLyricist());
        update.set("composer", song.getComposer());
        ktvMongoTemplate.updateFirst(query, update, KtvSong.class);
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(song.getCode())), KtvSong.class);
    }


    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvSong.class);
        return result.getDeletedCount();       //返回执行的条
    }

}
