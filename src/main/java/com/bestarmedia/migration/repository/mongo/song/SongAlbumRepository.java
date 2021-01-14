package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongAlbum;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class SongAlbumRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = songMongoTemplate.remove(query, SongAlbum.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public SongAlbum findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongAlbum.class);
    }

    public SongAlbum findByName(String name) {
        return songMongoTemplate.findOne(new Query(Criteria.where("albumName").is(name)), SongAlbum.class);
    }

    public SongAlbum insert(String albumName, String songName, String singer) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code"));
        SongAlbum songAlbum = songMongoTemplate.findOne(query, SongAlbum.class);
        int code = 1;
        if (songAlbum != null) {
            code = songAlbum.getCode() + 1;
        }
        SongAlbum insert = new SongAlbum(code, albumName, "", "从歌曲信息：" + songName + "-" + singer + " 导入");
        insert.setCreatedAt(new Date());
        insert.setUpdatedAt(new Date());
        insert.setCreateUser(0);
        insert.setUpdateUser(0);
        return songMongoTemplate.insert(insert);
    }

    private SongAlbum insert(SongAlbum songLanguage) {
        return songMongoTemplate.insert(songLanguage);
    }

    public List<SongAlbum> findAll() {
        return songMongoTemplate.findAll(SongAlbum.class);
    }

}
