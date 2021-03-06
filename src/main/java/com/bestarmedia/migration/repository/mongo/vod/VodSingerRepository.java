package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.model.mongo.vod.VodSinger;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class VodSingerRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public Page<VodSinger> findSinger(HashMap<String, Object> params, Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (params.containsKey("part_code")) {
            if (params.containsKey("part_code_parent_sub")) {
                criteria.and("part.code").in((ArrayList<Integer>) params.get("part_code_parent_sub"));
            } else {
                criteria.and("part.code").is(params.get("part_code"));
            }
        }
        if (params.containsKey("sex")) {
            criteria.and("sex").is(params.get("sex"));
        }
        if (params.containsKey("musician_name")) {
            Pattern pattern = Pattern.compile("^.*" + params.get("musician_name") + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.orOperator(Criteria.where("musician_name").regex(pattern), Criteria.where("simplicity").regex(pattern), Criteria.where("alias").regex(pattern));
        }
        criteria.and("status").is(1).and("musician_type").is(1);
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot").and(Sort.by(Sort.Direction.DESC, "musician_name")));
        Pageable pageable = PageRequest.of(currentPage, perPage);
        List<VodSinger> singers = vodMongoTemplate.find(query.with(pageable), VodSinger.class);
        return PageableExecutionUtils.getPage(singers, pageable, () -> vodMongoTemplate.count(query.skip(-1).limit(-1), VodSinger.class));
    }

    public VodSinger findSingerDetail(Integer code) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodSinger.class);
    }


    public VodSinger findSingerByName(String name) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("musician_name").is(name)), VodSinger.class);
    }

    public Integer findLastCode() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "code"));
        Pageable pageable = PageRequest.of(0, 1);
        List<VodSinger> singers = vodMongoTemplate.find(query.with(pageable), VodSinger.class);
        return singers == null || singers.isEmpty() ? 0 : singers.get(0).getCode();
    }

//    public VodSinger replace(VodSinger singer) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(singer.getCode())), VodSinger.class);
//        return vodMongoTemplate.insert(singer);
//    }

    public VodSinger insert(VodSinger singer) {
        return vodMongoTemplate.insert(singer);
    }


    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = vodMongoTemplate.remove(query, VodSinger.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public long countWarehousing() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return vodMongoTemplate.count(query, VodSinger.class);
//        return songMongoTemplate.findAll(SongInformation.class);
    }


    public List<VodSinger> indexWarehousingMusician(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        Pageable pageable = PageRequest.of(currentPage, perPage);
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return vodMongoTemplate.find(query.with(pageable), VodSinger.class);
    }


//    public long update(Integer singerCode, List<MusicianFileFormat> fileFormats) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("code").is(singerCode));
//        Update update = new Update();
//        update.set("file_formats", fileFormats);
//        UpdateResult updateResult = vodMongoTemplate.updateMulti(query, update, VodSinger.class);
//        return updateResult.getMatchedCount();       //返回执行的条
//    }
}
