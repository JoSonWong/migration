package com.bestarmedia.migration.repository.mongo.song;


import com.bestarmedia.migration.model.mongo.song.SongInformation;
import com.bestarmedia.migration.model.mongo.song.SongInformationSimple;
import com.bestarmedia.migration.model.mongo.vod.VodSong;
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
import java.util.List;

@Repository
public class SongInformationRepository {

    @Autowired
    @Qualifier(value = "songMongo")
    private MongoTemplate songMongoTemplate;

    /**
     * 根据编号获取歌曲信息
     *
     * @param code 歌曲编号
     * @return 歌曲信息
     */
    public SongInformation findByCode(Integer code) {
        return songMongoTemplate.findOne(new Query(Criteria.where("code").is(code)), SongInformation.class);
    }

    public List<SongInformation> findByNameAndSinger(String songName, List<String> singers) {
        return songMongoTemplate.find(new Query(Criteria.where("song_name").is(songName).and("singer.name").in(singers)), SongInformation.class);
    }

    public SongInformation insert(SongInformation song) {
        return songMongoTemplate.insert(song);
    }

//    public SongInformation replace(SongInformation song) {
//        songMongoTemplate.remove(new Query(Criteria.where("code").is(song.getCode())), SongInformation.class);
//        return songMongoTemplate.insert(song);
//    }


    public boolean musicianCodeIsUsed(Integer musicianCode) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        List<Criteria> orCriterias = new ArrayList<>();
        orCriterias.add(Criteria.where("singer.code").is(musicianCode));
        orCriterias.add(Criteria.where("lyricist.code").is(musicianCode));
        orCriterias.add(Criteria.where("composer.code").is(musicianCode));
        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        query.addCriteria(criteria);
        List<SongInformationSimple> list = songMongoTemplate.find(query, SongInformationSimple.class);
        return list != null && !list.isEmpty();
    }


    public long count() {
        Query query = new Query();
        Criteria criteria = new Criteria();
//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("singer.code").is(0));
//        orCriterias.add(Criteria.where("lyricist.code").is(0));
//        orCriterias.add(Criteria.where("composer.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        return songMongoTemplate.count(query, SongInformationSimple.class);
//        return songMongoTemplate.findAll(SongInformation.class);
    }

    public List<SongInformationSimple> findSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();

//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("singer.code").is(0));
//        orCriterias.add(Criteria.where("lyricist.code").is(0));
//        orCriterias.add(Criteria.where("composer.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));

        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return songMongoTemplate.find(query.with(pageable), SongInformationSimple.class);//查出歌曲列表
    }

    public long update(SongInformationSimple song) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(song.getCode()));
        Update update = new Update();
        update.set("singer", song.getSinger());
        update.set("lyricist", song.getLyricist());
        update.set("composer", song.getComposer());
        UpdateResult updateResult = songMongoTemplate.updateFirst(query, update, SongInformation.class);
        return updateResult.getMatchedCount();       //返回执行的条
    }

    public long cleanAllData() {
        Query query = new Query();
//        query.addCriteria(Criteria.where("code").gt(0));
        DeleteResult result = songMongoTemplate.remove(query, SongInformation.class);
        return result.getDeletedCount();       //返回执行的条
    }


    public List<SongInformation> indexSong(Integer currentPage, Integer perPage) {
        Query query = new Query();
        Criteria criteria = new Criteria();
//        List<Criteria> orCriterias = new ArrayList<>();
//        orCriterias.add(Criteria.where("singer.code").is(0));
//        orCriterias.add(Criteria.where("lyricist.code").is(0));
//        orCriterias.add(Criteria.where("composer.code").is(0));
//        criteria.orOperator(orCriterias.toArray(new Criteria[0]));
        query.addCriteria(criteria).with(Sort.by(Sort.Direction.DESC, "hot_sum"));
        Pageable pageable = PageRequest.of(currentPage, perPage);
        return songMongoTemplate.find(query.with(pageable), SongInformation.class);//查出歌曲列表
    }

}


