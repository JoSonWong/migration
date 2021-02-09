package com.bestarmedia.migration.repository.mongo.song;


import com.bestarmedia.migration.model.mongo.song.SongUgc;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SongUgcRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    /**
     * 根据编号获取歌曲信息
     *
     * @param code 歌曲编号
     * @return 歌曲信息
     */
    public SongUgc findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongUgc.class);
    }

    public int createNewCode() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code")).limit(1);
        SongUgc songUgc = songMongoTemplate.findOne(query, SongUgc.class);
        return songUgc == null ? 1 : songUgc.getCode() + 1;
    }

    public SongUgc insert(SongUgc song) {
        song.setCode(createNewCode());
        return songMongoTemplate.insert(song);
    }


    public long count() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        return songMongoTemplate.count(query, SongUgc.class);
    }

    public List<SongUgc> findSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return songMongoTemplate.find(query.with(pageable), SongUgc.class);//查出歌曲列表
    }


    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = songMongoTemplate.remove(query, SongUgc.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public List<SongUgc> indexSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return songMongoTemplate.find(query.with(pageable), SongUgc.class);//查出歌曲列表
    }


    public List<SongUgc> findAll() {
        return songMongoTemplate.findAll(SongUgc.class);
    }

}


