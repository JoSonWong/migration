package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.song.SongTag;
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
public class SongTagRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = songMongoTemplate.remove(query, SongTag.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public SongTag findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongTag.class);
    }

    public SongTag findByName(String name) {
        return songMongoTemplate.findOne(new Query(Criteria.where("tagName").is(name)), SongTag.class);
    }

//    public SongTag insert(String tagName, String songName, String singer) {
//        Query query = new Query();
//        query.with(Sort.by(Sort.Direction.DESC, "code"));
//        SongTag songTag = songMongoTemplate.findOne(query, SongTag.class);
//        int code = 1;
//        if (songTag != null) {
//            code = songTag.getCode() + 1;
//        }
//        SongTag insert = new SongTag(code, tagName, 0, "", 1, 0, "从歌曲信息：" + songName + "-" + singer + " 导入");
//        insert.setCreatedAt(new Date());
//        insert.setUpdatedAt(new Date());
//        insert.setCreateUser(0);
//        insert.setUpdateUser(0);
//        return songMongoTemplate.insert(insert);
//    }

    public SongTag insert(SongTag songTag) {
        return songMongoTemplate.insert(songTag);
    }

    public List<SongTag> findAll() {
        return songMongoTemplate.findAll(SongTag.class);
    }

}
