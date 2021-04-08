package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.TagSimple;
import com.bestarmedia.migration.model.mongo.song.SongMaterial;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public long cleanAllSingerImageData() {
        return songMongoTemplate.remove(new Query(Criteria.where("type").is(1)), SongMaterial.class).getDeletedCount();
    }

    public SongMaterial findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongMaterial.class);
    }

    public int createNewCode() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code")).limit(1);
        SongMaterial songUgc = songMongoTemplate.findOne(query, SongMaterial.class);
        return songUgc == null ? 1 : songUgc.getCode() + 1;
    }

    public SongMaterial insert(SongMaterial songMaterial) {
        songMaterial.setCode(createNewCode());
        return songMongoTemplate.insert(songMaterial);
    }

    public List<SongMaterial> findAll() {
        return songMongoTemplate.findAll(SongMaterial.class);
    }

    public long updateTag(int code, List<TagSimple> tag) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("tag", tag);
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongMaterial.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }


}
