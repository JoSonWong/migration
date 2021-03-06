package com.bestarmedia.migration.repository.mongo.song;

import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.song.SongSongVersion;
import com.bestarmedia.migration.model.mongo.song.SongSongVersionSimple;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SongSongVersionRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;


    public SongSongVersion insert(SongSongVersion vodSongVersion) {
        if (findByCode(vodSongVersion.getCode()) != null) {
            final int newVersionCode = createCode();
            vodSongVersion.setCode(newVersionCode);
            vodSongVersion.getVideoFileList().forEach(item ->
                    item.setCode(newVersionCode));
        }
        return songMongoTemplate.insert(vodSongVersion);
    }

//    public SongSongVersion replace(SongSongVersion vodSongVersion) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(vodSongVersion.getCode())), SongSongVersion.class);
//        return songMongoTemplate.insert(vodSongVersion);
//    }

    public long updateSinger(int songCode, List<CodeName> singer) {
        Query query = new Query();
        query.addCriteria(Criteria.where("song.code").is(songCode));
        Update update = new Update();
        update.set("singer", singer);
        UpdateResult updateResult = songMongoTemplate.updateMulti(query, update, SongSongVersion.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public long update(SongSongVersionSimple version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(version.getCode()));
        Update update = new Update();
        update.set("litigant", version.getLitigant());
        update.set("producer", version.getProducer());
        update.set("publisher", version.getPublisher());
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongSongVersion.class);
//    WriteResult upsert = vodMongoTemplate.updateMulti(query, update, "userList"); //查询到的全部更新
//    WriteResult upsert = vodMongoTemplate.updateFirst(query, update, "userList"); //查询更新第一条
//        UpdateResult upsert = songMongoTemplate.upsert(query, update, SongSongVersion.class);      //有则更新，没有则新增
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public long count() {
        Query query = new Query();
        Criteria criteria = new Criteria();

//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("litigant.code").is(0));
//        orCriterias.add(Criteria.where("producer.code").is(0));
//        orCriterias.add(Criteria.where("publisher.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));

        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "versions_hot_sum"));
        return songMongoTemplate.count(query, SongSongVersionSimple.class);
//        return songMongoTemplate.findAll(SongInformation.class);
    }


    public List<SongSongVersionSimple> findSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Pageable pageable = PageRequest.of(currentPage, perPage);
        Criteria criteria = new Criteria();

//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("litigant.code").is(0));
//        orCriterias.add(Criteria.where("producer.code").is(0));
//        orCriterias.add(Criteria.where("publisher.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));

        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "versions_hot_sum"));
        return songMongoTemplate.find(query.with(pageable), SongSongVersionSimple.class);
    }


    public boolean musicianCodeIsUsed(Integer musicianCode) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriterias = new ArrayList<>();
        orCriterias.add(Criteria.where("litigant.code").is(musicianCode));
        orCriterias.add(Criteria.where("producer.code").is(musicianCode));
        orCriterias.add(Criteria.where("publisher.code").is(musicianCode));
        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        query.addCriteria(criteria);
        List<SongSongVersionSimple> list = songMongoTemplate.find(query, SongSongVersionSimple.class);
        return list != null && !list.isEmpty();
    }


    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = songMongoTemplate.remove(query, SongSongVersion.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public List<SongSongVersion> indexVersion(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Pageable pageable = PageRequest.of(currentPage, perPage);
        Criteria criteria = new Criteria();
//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("litigant.code").is(0));
//        orCriterias.add(Criteria.where("producer.code").is(0));
//        orCriterias.add(Criteria.where("publisher.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "versions_hot_sum"));
        return songMongoTemplate.find(query.with(pageable), SongSongVersion.class);
    }


    public long updateIssueTime(Integer songCodeOld, Date date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("song_code_old").is(songCodeOld));
        Update update = new Update();
        update.set("issue_time", date);
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongSongVersion.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public SongSongVersion findByCode(Integer code) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(code));
        return songMongoTemplate.findOne(query, SongSongVersion.class);       //返回执行的条
    }


//    public List<SongVersionSimple> findVodSongVersion(Integer songCode) {
//        List<SongSongVersion> versions = songMongoTemplate.find(new Query(Criteria.where("song_code").is(songCode)), SongSongVersion.class);
//        List<SongVersionSimple> simples = new ArrayList<>();
//        if (versions != null) {
//            versions.forEach(item -> {
//                SongVersionSimple simple = new SongVersionSimple();
//                simple.setCode(item.getCode());
//                simple.setType(item.getType());
//                simple.setVersionsTypeCode(item.getVersionsType());
//                simple.setRecommend(item.getRecommend());
//                simple.setVersionHotSum(item.getVersionHotSum());
//                simple.setStatus(item.getStatus());
//                List<FileSimple> fileSimples = new ArrayList<>();
//                if (item.getVideoFileList() != null) {
//                    item.getVideoFileList().forEach(it ->
//                            fileSimples.add(new FileSimple(it.getCode(), it.getFormatName(), it.getStatus())));
//                }
//                simple.setFileSimples(fileSimples);
//                simples.add(simple);
//            });
//        }
//        return simples;
//    }


    public SongSongVersion findBySongNameAndSingerName(String songName, List<String> singers) {
        return songMongoTemplate.findOne(new Query(Criteria.where("song.name").is(songName).and("singer.name").in(singers)), SongSongVersion.class);
    }


    public int createCode() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "code")).limit(1);
        SongSongVersion songSongVersion = songMongoTemplate.findOne(query, SongSongVersion.class);
        int code = songSongVersion == null ? 0 : songSongVersion.getCode();
        return code < 1000000 ? 1000000 : (code + 1);
    }


    public long cleanAllMP3Data() {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(2).and("file.accompaniment_audio_file_path").ne(""));
        DeleteResult result = songMongoTemplate.remove(query, SongSongVersion.class);
        return result.getDeletedCount();       //返回执行的条
    }

    public long cleanAllMysqlMP3Data() {
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("type").is(2).and("file.accompaniment_audio_file_path").is(""),
                Criteria.where("type").is(1).and("file.file_path").exists(false));
        Query query = new Query();
        query.addCriteria(criteria);
        DeleteResult result = songMongoTemplate.remove(query, SongSongVersion.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public List<SongSongVersion> findSongVersion(Integer songCode) {
        return songMongoTemplate.find(new Query(Criteria.where("song.code").is(songCode)), SongSongVersion.class);
    }

    public long updateOriginalByRemark(String fileRemark, String originalFilePath) {
        return updateFilePath(fileRemark, "file.$.original_audio_file_path", originalFilePath);
    }

    public long updateAccompaniment(String fileRemark, String accompanimentFile) {//返回执行的条
        return updateFilePath(fileRemark, "file.$.accompaniment_audio_file_path", accompanimentFile);
    }

    public long updateLyricFile(String fileRemark, String lyricFile) {
        return updateFilePath(fileRemark, "file.$.lyric_file_path", lyricFile);
    }

    public long updateFilePath(String fileRemark, String field, String filePath) {
        Query query = new Query();
        query.addCriteria(Criteria.where("file.remark").is(fileRemark));
        Update update = new Update();
        update.set(field, filePath);
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongSongVersion.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }


    public long update(int songCode, SongSongVersionSimple version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("song.code").is(songCode));
        Update update = new Update();
        update.set("litigant", version.getLitigant());
        update.set("producer", version.getProducer());
        update.set("publisher", version.getPublisher());
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongSongVersion.class);
//    WriteResult upsert = vodMongoTemplate.updateMulti(query, update, "userList"); //查询到的全部更新
//    WriteResult upsert = vodMongoTemplate.updateFirst(query, update, "userList"); //查询更新第一条
//        UpdateResult upsert = songMongoTemplate.upsert(query, update, SongSongVersion.class);      //有则更新，没有则新增
        return updateResult.getMatchedCount();       //返回执行的条
    }


    public List<SongSongVersion> findSingerCode0() {
        return songMongoTemplate.find(new Query(Criteria.where("singer.code").is(0)), SongSongVersion.class);
    }

}
