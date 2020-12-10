package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.SearchKeyword;
import com.bestarmedia.migration.model.mongo.VideoFile;
import com.bestarmedia.migration.model.mongo.song.*;
import com.bestarmedia.migration.model.mysql.Language;
import com.bestarmedia.migration.model.mysql.Musician;
import com.bestarmedia.migration.model.mysql.Part;
import com.bestarmedia.migration.model.mysql.Song;
import com.bestarmedia.migration.repository.mongo.song.*;
import com.bestarmedia.migration.repository.mysql.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class MigrateMySQL2MongoSongService extends MigrateBase {

    private final MysqlMusicianRepository mysqlMusicianRepository;
    private final MysqlPartRepository mysqlPartRepository;
    private final MysqlSongRepository mysqlSongRepository;
    private final MysqlSongTypeRepository mysqlSongTypeRepository;
    private final MysqlLanguageRepository mysqlLanguageRepository;

    private final SongMusicianRepository songMusicianRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongInformationRepository songInformationRepository;
    private final SongLanguageRepository songLanguageRepository;
    private final SongSongTypeRepository songSongTypeRepository;
    private final SongAreaRepository songAreaRepository;


    private int versionCount = 0;
    private int songCount = 0;

    private int musicianCount = 0;
    private int musicianMergeCount = 0;
    private int musicianGiveUpCount = 0;

    @Autowired
    public MigrateMySQL2MongoSongService(MysqlMusicianRepository mysqlMusicianRepository,
                                         MysqlPartRepository mysqlPartRepository,
                                         MysqlSongRepository mysqlSongRepository,
                                         MysqlSongTypeRepository mysqlSongTypeRepository,
                                         MysqlLanguageRepository mysqlLanguageRepository,

                                         SongMusicianRepository songMusicianRepository,
                                         SongSongVersionRepository songSongVersionRepository,
                                         SongInformationRepository songInformationRepository,
                                         SongLanguageRepository songLanguageRepository,
                                         SongSongTypeRepository songSongTypeRepository,
                                         SongAreaRepository songAreaRepository) {
        this.mysqlMusicianRepository = mysqlMusicianRepository;
        this.mysqlPartRepository = mysqlPartRepository;
        this.mysqlSongRepository = mysqlSongRepository;
        this.mysqlSongTypeRepository = mysqlSongTypeRepository;
        this.mysqlLanguageRepository = mysqlLanguageRepository;

        this.songMusicianRepository = songMusicianRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songInformationRepository = songInformationRepository;
        this.songLanguageRepository = songLanguageRepository;
        this.songSongTypeRepository = songSongTypeRepository;
        this.songAreaRepository = songAreaRepository;
    }


    public String migrate() {
        long currentTimeMillis = System.currentTimeMillis();
        mergeLanguage();
        mergePart();
        mergeSongType();
        migrateMusician();
        migrateSong();
        String tip = "MySQL 数据迁移 Mongo.Song 总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    private void mergePart() {
        List<Part> parts = mysqlPartRepository.findAll();
        long delCount = songAreaRepository.cleanAllData();
        System.out.println("清除地区信息数量量:" + delCount);
        parts.forEach(item -> {
            try {
                SongArea songArea = new SongArea();
                songArea.setCode(item.getId());
                songArea.setName(item.getName());
                songArea.setParentCode(0);
                songArea.setIsShow(item.getIsShow());
                songArea.setSort(item.getSort());
                songArea.setCreateUser(item.getCreateUser());
                songArea.setUpdateUser(item.getUpdateUser());
                songArea.setCreatedAt(item.getCreatedAt());
                songArea.setUpdatedAt(item.getUpdatedAt());
                songArea.setDeletedAt(item.getDeletedAt());
                SongArea save = songAreaRepository.insert(songArea);
                System.out.println("保存地区信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void mergeLanguage() {
        List<Language> languages = mysqlLanguageRepository.findAll();
        long delCount = songLanguageRepository.cleanAllData();
        System.out.println("清除语种信息数量量:" + delCount);
        languages.forEach(item -> {
            try {
                SongLanguage language = new SongLanguage();
                language.setCode(item.getId());
                language.setName(item.getLanguageName());
                language.setParentCode(0);
                language.setIsShow(item.getIsShow());
                language.setSort(item.getSort());
                language.setCreateUser(item.getCreateUser());
                language.setUpdateUser(item.getUpdateUser());
                language.setCreatedAt(item.getCreatedAt());
                language.setUpdatedAt(item.getUpdatedAt());
                language.setDeletedAt(item.getDeletedAt());
                SongLanguage save = songLanguageRepository.insert(language);
                System.out.println("保存语种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeSongType() {
        List<com.bestarmedia.migration.model.mysql.SongType> songTypes = mysqlSongTypeRepository.findAll();
        long delCount = songSongTypeRepository.cleanAllData();
        System.out.println("清除曲种信息数量量:" + delCount);

        songTypes.forEach(item -> {
            try {
                SongSongType songType = new SongSongType();
                songType.setCode(item.getId());
                songType.setName(item.getName());
                songType.setParentCode(item.getParentId());
                songType.setIsShow(item.getIsShow());
                songType.setSort(item.getSort());
                songType.setCreateUser(item.getCreateUser());
                songType.setUpdateUser(item.getUpdateUser());
                songType.setCreatedAt(item.getCreatedAt());
                songType.setUpdatedAt(item.getUpdatedAt());
                songType.setDeletedAt(item.getDeletedAt());
                SongSongType save = songSongTypeRepository.insert(songType);
                System.out.println("保存曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public String migrateMusician() {
        long delCount = songMusicianRepository.cleanAllData();
        System.out.println("清除音乐人信息数量量:" + delCount);
        musicianCount = 0;
        musicianMergeCount = 0;
        musicianGiveUpCount = 0;
        long cur = System.currentTimeMillis();
        int count = mysqlMusicianRepository.getMusicianSameNameCount();
        final int pageSize = 10000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
//        size = 1;
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = 0; i < size; i++) {
            migrateMusician(i * pageSize, pageSize);
        }
        String text = "MySQL 音乐人总量" + count + " 合并同名迁移音乐人总数：" + musicianCount + " 合并音乐人数量：" + musicianMergeCount
                + " 丢弃音乐人数量：" + musicianGiveUpCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }

    private SongMusician createSongMusician(Musician item) {
        SongMusician singer = new SongMusician();
        singer.setCode(item.getMusicianId());
        singer.setMusicianName(CommonUtil.deleteSpaceAndUpperFirst(item.getMusicianName()));
        singer.setSimplicity(StringUtils.isEmpty(item.getMusicianInitial()) ? "" : item.getMusicianInitial().toUpperCase());
        singer.setWordCount(item.getWordCount());

        List<Integer> musicianTypes = new ArrayList<>();
        List<String> musicianTypeTexts = new ArrayList<>();
        if (item.getIsSinger() == 1) {
            musicianTypes.add(1);
            musicianTypeTexts.add("歌手");
        }
        if (item.getIsComposer() == 1) {
            musicianTypes.add(2);
            musicianTypeTexts.add("曲作者");
        }
        if (item.getIsLyricist() == 1) {
            musicianTypes.add(3);
            musicianTypeTexts.add("词作者");
        }
        if (item.getIsLitigant() == 1) {
            musicianTypes.add(4);
            musicianTypeTexts.add("诉讼权利人");
        }
        if (item.getIsProducer() == 1) {
            musicianTypes.add(5);
            musicianTypeTexts.add("制作权利人");
        }
        if (item.getIsPublisher() == 1) {
            musicianTypes.add(6);
            musicianTypeTexts.add("出品权利人");
        }
        singer.setMusicianType(musicianTypes);
//                    singer.setMusicianTypeText(musicianTypeTexts);
        singer.setRole(item.getRole());
        singer.setSex(item.getSex());
        singer.setHot(0L);
        singer.setHotSum(item.getHot().longValue());
//        singer.setBirthday(item.getBirthday() == null ? DateUtil.string2DateTime("1970-01-01 00:00:00") : item.getBirthday());
        singer.setBirthday(item.getBirthday());
        singer.setPart(null);
        Optional<Part> part;
        if (item.getPart() > 0 && (part = this.mysqlPartRepository.findById(item.getPart())).isPresent()) {
            singer.setPart(new CodeName(item.getPart(), part.get().getName()));
        }
        singer.setImgFilePath(item.getImgFilePath());
        singer.setStatus(item.getStatus());
        List<String> alias = new ArrayList<>();
        //合并到曾用名
        if (!StringUtils.isEmpty(item.getSimpBynameOne())) {//别名1
            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameOne()));
        } else if (!StringUtils.isEmpty(item.getSimpBynameTwo())) {//别名2
            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameTwo()));
        } else if (!StringUtils.isEmpty(item.getSimpBynameThree())) {//别名3
            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameThree()));
        } else if (!StringUtils.isEmpty(item.getSimpBynameFour())) {//别名4
            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameFour()));
        }
        singer.setAlias(alias);//别名

        List<SearchKeyword> searchKeywords = new ArrayList<>();
        SearchKeyword searchKeyword = new SearchKeyword();
        searchKeyword.setType(0);
        searchKeyword.setCode(singer.getCode());
        List<String> keywords = new ArrayList<>();
        keywords.add(singer.getMusicianName());
        if (!StringUtils.isEmpty(singer.getSimplicity())) {
            keywords.add(singer.getSimplicity());
        }
        if (!alias.isEmpty()) {
            keywords.addAll(alias);
        }
        searchKeyword.setKeywords(keywords);
        searchKeywords.add(searchKeyword);

        singer.setSearchKeywords(searchKeywords);

        singer.setMold(1);
        singer.setRemark("");
        singer.setCreatedAt(item.getCreatedAt());
        singer.setUpdatedAt(item.getUpdatedAt());
        singer.setDeletedAt(item.getDeletedAt());
        singer.setCreateUser(item.getCreateUser());
        singer.setUpdateUser(item.getUpdateUser());

        return singer;
    }


    private void migrateMusician(int index, int pageSize) {
        try {
            List<Musician> list = mysqlMusicianRepository.getMusicianSameName(index, pageSize);
            Map<String, Musician> map = new HashMap<>();
            list.forEach(item -> {
                String musicianName = CommonUtil.deleteSpaceAndUpperFirst(item.getMusicianName());
                item.setMusicianName(musicianName);
                if (map.containsKey(musicianName)) {
                    musicianMergeCount++;
                    Musician musician = map.get(musicianName);
                    if ((musician.getStatus() != 1 && item.getStatus() == 1)
                            || (StringUtils.isEmpty(musician.getMusicianInitial()) && !StringUtils.isEmpty(item.getMusicianInitial()))
                            || (musician.getWordCount() < 1 && item.getWordCount() > 0)
                            || (musician.getHot() < item.getHot())
                            || (StringUtils.isEmpty(musician.getImgFilePath()) && !StringUtils.isEmpty(item.getImgFilePath()))) {

                        Musician realMusician = item;

                        //补充信息
                        if (realMusician.getPart() == 0 && musician.getPart() != 0) {
                            realMusician.setPart(musician.getPart());
                        }
                        if (realMusician.getIsSinger() == 0 && musician.getIsSinger() != 0) {
                            realMusician.setIsSinger(musician.getIsSinger());
                        }
                        if (realMusician.getIsLyricist() == 0 && musician.getIsLyricist() != 0) {
                            realMusician.setIsLyricist(musician.getIsLyricist());
                        }
                        if (realMusician.getIsComposer() == 0 && musician.getIsComposer() != 0) {
                            realMusician.setIsComposer(musician.getIsComposer());
                        }
                        if (realMusician.getIsLitigant() == 0 && musician.getIsLitigant() != 0) {
                            realMusician.setIsLitigant(musician.getIsLitigant());
                        }
                        if (realMusician.getIsProducer() == 0 && musician.getIsProducer() != 0) {
                            realMusician.setIsProducer(musician.getIsProducer());
                        }
                        if (realMusician.getIsPublisher() == 0 && musician.getIsPublisher() != 0) {
                            realMusician.setIsPublisher(musician.getIsPublisher());
                        }
                        map.put(musicianName, realMusician);
                        try {
                            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 合并音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(realMusician));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            musicianGiveUpCount++;
                            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 丢弃音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    map.put(musicianName, item);
                }
            });
            for (Map.Entry<String, Musician> entry : map.entrySet()) {
                Musician musician = entry.getValue();
                SongMusician save = songMusicianRepository.insert(createSongMusician(musician));
                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存音乐人信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                musicianCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String migrateSong() {
        System.out.println("清除版本信息数量:" + songSongVersionRepository.cleanAllData());
        System.out.println("清除歌曲信息数量:" + songInformationRepository.cleanAllData());
        versionCount = 0;
        songCount = 0;
        long cur = System.currentTimeMillis();
        int count = mysqlSongRepository.getSongSameNameCount();
        final int pageSize = 10000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = 0; i < size; i++) {
            migrateSong2CmsV7(i * pageSize, pageSize);
        }
        String text = "处理歌曲耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒  版本数量：" + versionCount + " 合并后歌曲总数：" + songCount;
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }

    private void migrateSong2CmsV7(int index, int pageSize) {
        try {
            long cur = System.currentTimeMillis();
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 开始处理歌曲index：" + index);
            Map<String, SongInformation> map = new HashMap<>();
            List<Song> list = mysqlSongRepository.getSongSameName(index, pageSize);
            list.forEach(item -> {
                try {
                    String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(item.getSongName()));
                    String key = (songName + ";" + item.getSongId()).toUpperCase();
                    if (!(StringUtils.isEmpty(item.getSinger()) || "[]".equals(item.getSinger()) || "[\"\"]".equals(item.getSinger()) || "[\" \"]".equals(item.getSinger()))) {
                        List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(item.getSinger(), new TypeReference<List<String>>() {
                        });
                        singerNames.sort(Comparator.naturalOrder());
                        key = (songName + ";" + CommonUtil.OBJECT_MAPPER.writeValueAsString(singerNames)).toUpperCase();
                    }
                    if (map.containsKey(key)) {
                        SongInformation vodSong = map.get(key);
                        createSongVersion(vodSong.getCode(), vodSong.getSongName(), item);
                        vodSong.setHot(0L);
                        vodSong.setHotSum(vodSong.getHotSum() + item.getHot());

                        if ((vodSong.getLyricist() == null || vodSong.getLyricist().isEmpty()) && (!StringUtils.isEmpty(item.getLyricistMid()) || !StringUtils.isEmpty(item.getLyricist()))) {//词作者
                            vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
                            System.out.println("补充词作者：" + item.getId() + "  " + item.getLyricistMid() + " " + item.getLyricist());
                        }
                        if ((vodSong.getComposer() == null || vodSong.getComposer().isEmpty()) && (!StringUtils.isEmpty(item.getComposerMid()) || !StringUtils.isEmpty(item.getComposer()))) {//曲作者
                            vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
                            System.out.println("补充曲作者：" + item.getId() + "  " + item.getComposerMid() + "  " + item.getComposer());
                        }
                        if (StringUtils.isEmpty(vodSong.getSongInitial()) && !StringUtils.isEmpty(item.getSongInitial())) {
                            vodSong.setSongInitial(item.getSongInitial());
                            System.out.println("补充简拼：" + item.getId() + "  " + item.getSongInitial());
                        }
                        if ((vodSong.getWordCount() == null || vodSong.getWordCount() == 0) && (item.getWordCount() != null && item.getWordCount() > 0)) {
                            vodSong.setWordCount(item.getWordCount());
                            System.out.println("补充字数：" + item.getId() + "  " + item.getWordCount());
                        }
                        if ((vodSong.getSongType() == null || vodSong.getSongType().getCode() == 0) && (item.getSongType() != null)) {
                            SongType songType = new SongType(item.getSongType().getId(), item.getSongType().getName(), item.getSongType().getId(), item.getSongType().getName());
                            Optional<com.bestarmedia.migration.model.mysql.SongType> optional;
                            if (item.getSongType().getParentId() > 0 && (optional = this.mysqlSongTypeRepository.findById(item.getSongType().getParentId())).isPresent()) {
                                songType.setParentCode(optional.get().getId());
                                songType.setParentName(optional.get().getName());
                            }
                            vodSong.setSongType(songType);
                            System.out.println("补充歌曲类型：" + item.getId() + "  " + item.getSongType().getName());
                        }
                        if ((vodSong.getLanguage() == null || vodSong.getLanguage().getCode() == 0) && (item.getLanguage() != null)) {
                            vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
                            System.out.println("补充语种：" + item.getId() + "  " + item.getLanguage().getLanguageName());
                        }
                        if ((vodSong.getRecommend() == null || vodSong.getRecommend() == 0) && (item.getSofthard() != null && item.getSofthard() > 0)) {
                            vodSong.setRecommend(item.getSofthard());
                            System.out.println("补充推荐歌曲：" + item.getId() + "  " + item.getSofthard());
                        }
                        if ((vodSong.getStatus() == null || vodSong.getStatus() == 0) && (item.getStatus() != null && item.getStatus() > 0)) {
                            vodSong.setStatus(item.getStatus());
                            System.out.println("补充上架状态：" + item.getId() + "  " + item.getStatus());
                        }
//                        if (StringUtils.isEmpty(vodSong.getNoteOne()) && !StringUtils.isEmpty(item.getNoteOne())) {
//                            vodSong.setNoteOne(item.getNoteOne());
//                            System.out.println("补充备注1：" + item.getId() + "  " + item.getNoteOne());
//                        }
//                        if (StringUtils.isEmpty(vodSong.getNoteTwo()) && !StringUtils.isEmpty(item.getNoteTwo())) {
//                            vodSong.setNoteTwo(item.getNoteTwo());
//                            System.out.println("补充备注2：" + item.getId() + "  " + item.getNoteTwo());
//                        }
//                        if (StringUtils.isEmpty(vodSong.getNoteThree()) && !StringUtils.isEmpty(item.getNoteThree())) {
//                            vodSong.setNoteThree(item.getNoteThree());
//                            System.out.println("补充备注3：" + item.getId() + "  " + item.getNoteThree());
//                        }
                        map.put(key, vodSong);

                    } else {
                        SongInformation vodSong = new SongInformation();
                        vodSong.setCode(item.getSongId());
                        vodSong.setSongInitial(StringUtils.isEmpty(item.getSongInitial()) ? "" : item.getSongInitial().toUpperCase());
                        vodSong.setSongName(songName);
                        vodSong.setSinger(getIdNames(item.getSingerMid(), item.getSinger()));
                        vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
                        vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
                        vodSong.setWordCount(item.getWordCount());

                        SongType songType = new SongType(item.getSongType().getId(), item.getSongType().getName(), item.getSongType().getId(), item.getSongType().getName());
                        Optional<com.bestarmedia.migration.model.mysql.SongType> optional;
                        if (item.getSongType().getParentId() > 0 && (optional = this.mysqlSongTypeRepository.findById(item.getSongType().getParentId())).isPresent()) {
                            songType.setParentCode(optional.get().getId());
                            songType.setParentName(optional.get().getName());
                        }
                        vodSong.setSongType(songType);

                        vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
                        vodSong.setTag(new ArrayList<>());
                        vodSong.setRecommend(item.getSofthard());
                        vodSong.setHot(0L);
                        vodSong.setHotSum(item.getHot());
                        vodSong.setStatus(item.getStatus());
                        vodSong.setNoteOne("");
                        vodSong.setNoteTwo("");
                        vodSong.setNoteThree("");
                        vodSong.setCreatedAt(item.getCreatedAt());
                        vodSong.setUpdatedAt(item.getUpdatedAt());
                        vodSong.setCreateUser(item.getCreateUser());
                        vodSong.setUpdateUser(item.getUpdateUser());
                        createSongVersion(vodSong.getCode(), vodSong.getSongName(), item);
                        map.put(key, vodSong);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 处理数据耗时：" + (System.currentTimeMillis() - cur));
            cur = System.currentTimeMillis();
            for (Map.Entry<String, SongInformation> entry : map.entrySet()) {
                SongInformation save = songInformationRepository.insert(entry.getValue());
                songCount++;
                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存歌曲信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            }
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 存储数据耗时：" + (System.currentTimeMillis() - cur));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SongSongVersion createSongVersion(Integer songId, String songName, Song song) {
        SongSongVersion version = new SongSongVersion();
        version.setCode(song.getSongId());//以旧歌曲版本命名版本id
//        version.setSongCode(songId);
        version.setSong(new CodeName(songId, songName));
        version.setSongCodeOld(song.getSongId());
        CodeName edition = getEdition(song.getVideoType());
        version.setType(1);
        version.setVersionsType(edition.getCode());
        version.setVersionsName(edition.getName());
        version.setSource(song.getSongSource() + "");
        version.setAlbum(null);
        version.setSinger(getIdNames(song.getSingerMid(), song.getSinger()));
        version.setLitigant(getIdNames(song.getLitigantMid(), song.getLitigant()));//诉讼权利人
        version.setProducer(getIdNames(song.getProducerMid(), song.getProducer()));//制作权利人
        version.setPublisher(getIdNames(song.getPublisherMid(), song.getPublisher()));//出品权利人
        version.setIncreaseHot(song.getHot());
        version.setVersionHot(0L);
        version.setVersionHotSum(song.getHot());
//        version.setIssueTime(DateUtil.string2DateTime("1970-01-01 00:00:00"));
        version.setIssueTime(null);
        version.setRecommend(song.getSofthard());
        version.setStatus(song.getStatus());
        version.setCreatedAt(song.getCreatedAt());
        version.setUpdatedAt(song.getUpdatedAt());
        version.setDeletedAt(song.getDeletedAt());
        version.setCreateUser(song.getCreateUser());
        version.setUpdateUser(song.getUpdateUser());
        version.setNoteOne(song.getNoteOne());
        version.setNoteTwo(song.getNoteTwo());
        version.setNoteThree(song.getNoteThree());
        List<VideoFile> files = new ArrayList<>();
        files.add(createFileDto(song));
        version.setVideoFileList(files);
        SongSongVersion save = songSongVersionRepository.insert(version);
        try {
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存版本信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
        } catch (Exception e) {
            e.printStackTrace();
        }
        versionCount++;
        return save;
    }

}