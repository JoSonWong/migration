package com.bestarmedia.migration.repository.mongo.ktv;


import com.bestarmedia.migration.model.mongo.ktv.KtvUgc;
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
public class KtvUgcRepository {

    @Autowired
    @Qualifier(value = "ktvMongo")
    private MongoTemplate ktvMongoTemplate;

    /**
     * 根据编号获取歌曲信息
     *
     * @param code 歌曲编号
     * @return 歌曲信息
     */
    public KtvUgc findByCode(Integer code) {
        return ktvMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), KtvUgc.class);
    }

    public int createNewCode() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code")).limit(1);
        KtvUgc songUgc = ktvMongoTemplate.findOne(query, KtvUgc.class);
        return songUgc == null ? 1 : songUgc.getCode() + 1;
    }

    public KtvUgc insert(KtvUgc song) {
        song.setCode(createNewCode());
        return ktvMongoTemplate.insert(song);
    }


    public long count() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        return ktvMongoTemplate.count(query, KtvUgc.class);
    }

    public List<KtvUgc> findSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return ktvMongoTemplate.find(query.with(pageable), KtvUgc.class);//查出歌曲列表
    }


    public long cleanAllData() {
        Query query = new Query();
        DeleteResult result = ktvMongoTemplate.remove(query, KtvUgc.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public List<KtvUgc> indexSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria);
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return ktvMongoTemplate.find(query.with(pageable), KtvUgc.class);//查出歌曲列表
    }

}


