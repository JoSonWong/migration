package com.bestarmedia.migration.repository.mongo.vod;


import com.bestarmedia.migration.model.mongo.vod.VodSong;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VodSongRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;


    public VodSong findByIdVodSong(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code).and("status").is(1)), VodSong.class);
    }

    /**
     * 获取歌曲信息（上架状态）
     */
    public VodSong findByCodeEnable(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code).and("status").is(1)), VodSong.class);
    }

    /**
     * 根据编号获取歌曲信息
     *
     * @param code 歌曲编号
     * @return 歌曲信息
     */
    public VodSong findByCodeNotStatus(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodSong.class);
    }

    public VodSong insert(VodSong song) {
        return vodMongoTemplate.insert(song);
    }

    public VodSong replace(VodSong song) {
        vodMongoTemplate.remove(new Query(Criteria.where("code").is(song.getCode())), VodSong.class);
        return vodMongoTemplate.insert(song);
    }


    public VodSong update(VodSong song) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(song.getCode()));
        Update update = new Update();
        update.set("song_name", song.getSongName());
        update.set("singer", song.getSinger());
        update.set("lyricist", song.getLyricist());
        update.set("composer", song.getComposer());
        UpdateResult result = vodMongoTemplate.updateFirst(query, update, VodSong.class);
        long count = result.getMatchedCount();
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(song.getCode())), VodSong.class);
    }


    public List<VodSong> findByNameAndSinger(String songName, List<String> singers) {
        return vodMongoTemplate.find(new Query(Criteria.where("song_name").is(songName).and("singer.name").in(singers)), VodSong.class);
    }

    public List<VodSong> findByName(String songName) {
        return vodMongoTemplate.find(new Query(Criteria.where("song_name").is(songName)), VodSong.class);
    }
}
