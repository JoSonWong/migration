package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.SearchKeyword;
import com.bestarmedia.migration.model.mongo.song.SongMusician;
import com.bestarmedia.migration.model.mongo.song.SongMusicianSimple;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongMusicianRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongMusician findSingerByName(String name) {
        return songMongoTemplate.findOne(new Query(Criteria.where("musician_name").is(name)), SongMusician.class);
    }

    public long removeByCode(Integer code) {
        return songMongoTemplate.remove(new Query(Criteria.where("code").is(code)), SongMusician.class).getDeletedCount();
    }

//    public SongMusician replace(SongMusician musician) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(musician.getCode())), SongMusician.class);
//        return songMongoTemplate.insert(musician);
//    }

    public SongMusician insert(SongMusician singer) {
        return songMongoTemplate.insert(singer);
    }

    public long updateMusicianTypes(Integer code, List<Integer> musicianTypes) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("musician_type", musicianTypes);
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongMusician.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public long updateSearchKeywords(Integer code, List<SearchKeyword> searchKeywords) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("search_keywords", searchKeywords);
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongMusician.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public int getMaxCode() {
        SongMusician songMusician = songMongoTemplate.findOne(new Query(Criteria.where("code").gt(0)).with(Sort.by(Sort.Direction.DESC, "_id")).limit(1), SongMusician.class);
//        List<SongMusician> songMusicians = songMongoTemplate.find(new Query()//条件
//                .with(Sort.by(Sort.Direction.DESC, "code")).limit(1), SongMusician.class);//排序
        return songMusician == null ? 200000 : songMusician.getCode();
//        return songMongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "code")).skip(0).limit(1), SongMusician.class).get(0).getCode();
    }


    public long count() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.ASC, "hot_sum"));
        return songMongoTemplate.count(query, SongMusicianSimple.class);
//        return songMongoTemplate.findAll(SongInformation.class);
    }

    public List<SongMusicianSimple> findMusician(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Pageable pageable = PageRequest.of(currentPage, perPage);
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return songMongoTemplate.find(query.with(pageable), SongMusicianSimple.class);
    }

    public long updateImageFilePathEmpty() {
        Query query = new Query();
        query.addCriteria(Criteria.where("img_file_path").is("http://mt.beidousat.com/data/Img/SingerImg/7fc81ce72a719b24960f91ba25ac2feb.jpg"));
        Update update = new Update();
        update.set("img_file_path", "");
        UpdateResult updateResult = songMongoTemplate.updateMulti(query, update, SongMusician.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }


    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gte(0));
        DeleteResult result = songMongoTemplate.remove(query, SongMusician.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public long countWarehousing() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return songMongoTemplate.count(query, SongMusicianSimple.class);
//        return songMongoTemplate.findAll(SongInformation.class);
    }


    public List<SongMusician> indexWarehousingMusician(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(Criteria.where("mold").is(1));
        Pageable pageable = PageRequest.of(currentPage, perPage);
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return songMongoTemplate.find(query.with(pageable), SongMusician.class);
    }


}
