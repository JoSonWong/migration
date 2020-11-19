package com.bestarmedia.migration.repository.mongo.vod;

import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.Auditing;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.vod.VodSongVersion;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class VodSongVersionRepository {

    @Autowired
    @Qualifier(value = "vodMongo")
    private MongoTemplate vodMongoTemplate;

    public List<String> findAllFileFormat() {
        Aggregation aggregationCount = Aggregation.newAggregation(
                Aggregation.project("file"), Aggregation.group("file.format_name").count().as("sum"));
        AggregationResults<Auditing> aggregationSumResult = vodMongoTemplate.aggregate(aggregationCount, "vod_song_version", Auditing.class);
        List<Auditing> versions = aggregationSumResult.getMappedResults();
        Map<String, Boolean> format = new HashMap<>();
        if (versions != null) {
            versions.forEach(item -> {
                String[] formats = item.get_id().split(",");
                for (String f : formats) {
                    format.put(f, true);
                }
            });
        }
        List<String> formats = new ArrayList<>();
        Set<Map.Entry<String, Boolean>> entries = format.entrySet();
        for (Map.Entry<String, Boolean> entry : entries) {
            formats.add(entry.getKey());
        }
        return formats;
    }

    public List<VodSongVersion> findVodSongVersion(Integer songCode) {
        return vodMongoTemplate.find(new Query(Criteria.where("song_code").is(songCode).and("versions_type").is(1).and("status").is(1)), VodSongVersion.class);
    }

    public List<VodSongVersion> findSongNextVersion(Integer code) {
        return vodMongoTemplate.find(new Query(Criteria.where("song_code").is(code).and("versions_type").is(1).and("status").is(1)), VodSongVersion.class);
    }

    public VodSongVersion findById(String versionCode) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(versionCode).and("status").is(1)), VodSongVersion.class);
    }

    public VodSongVersion findByIdNotStatus(String versionCode) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(versionCode)), VodSongVersion.class);
    }


    public VodSongVersion findBySongCodeOld(Integer songCodeOld) {
        return vodMongoTemplate.findOne(new Query(Criteria.where("song_code_old").is(songCodeOld)), VodSongVersion.class);
    }


    public void findSongInfo() {
        try {
            LookupOperation lookupOperation = LookupOperation.newLookup().
                    from("vod_song").  //关联从表名
                    localField("song_code").     //主表关联字段
                    foreignField("code").//从表关联的字段
                    as("song_doc");   //查询结果名
            Criteria criteria = Criteria.where("song_doc.song_name").regex("吻别");
            AggregationOperation match = Aggregation.match(criteria);
            Aggregation counts = Aggregation.newAggregation(match, lookupOperation);
//        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
            List<Map> results = vodMongoTemplate.aggregate(counts, VodSongVersion.class, Map.class).getMappedResults();
            //上面的student必须是查询的主表名
            System.out.println(CommonUtil.OBJECT_MAPPER.writeValueAsString(results));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public VodSongVersion save(VodSongVersion songVersion) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(songVersion.getCode())), VodSongVersion.class);
//        return vodMongoTemplate.insert(songVersion);
//    }

    //    public long update(Singer singer) {
//        Query query = new Query();
//        query.addCriteria(Criteria.where("_id").is(singer.get_id()));
//        Update update = Update.update("name", "zzzzz");
////    WriteResult upsert = vodMongoTemplate.updateMulti(query, update, "userList"); //查询到的全部更新
////    WriteResult upsert = vodMongoTemplate.updateFirst(query, update, "userList"); //查询更新第一条
//        UpdateResult upsert = vodMongoTemplate.upsert(query, update, "userList");      //有则更新，没有则新增
//        return upsert.getMatchedCount();       //返回执行的条
//    }
    public VodSongVersion update(Integer code, List<CodeName> litigant, List<CodeName> producer, List<CodeName> publisher, Date issueTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        Update update = new Update();
        update.set("litigant", litigant);
        update.set("producer", producer);
        update.set("publisher", publisher);
        update.set("issue_time", issueTime);
        UpdateResult result = vodMongoTemplate.updateFirst(query, update, VodSongVersion.class);
        long count = result.getMatchedCount();
        return vodMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), VodSongVersion.class);
    }


    public VodSongVersion insert(VodSongVersion vodSongVersion) {
        return vodMongoTemplate.insert(vodSongVersion);
    }

//    public VodSongVersion replace(VodSongVersion vodSongVersion) {
//        vodMongoTemplate.remove(new Query(Criteria.where("code").is(vodSongVersion.getCode())), VodSongVersion.class);
//        return vodMongoTemplate.insert(vodSongVersion);
//    }

    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = vodMongoTemplate.remove(query, VodSongVersion.class);
        return result.getDeletedCount();       //返回执行的条
    }
}
