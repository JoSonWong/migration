//package com.bestarmedia.migration.service;
//
//
//import com.bestarmedia.migration.misc.CommonUtil;
//import com.bestarmedia.migration.misc.DateUtil;
//import com.bestarmedia.migration.model.mongo.CodeName;
//import com.bestarmedia.migration.model.mongo.SearchKeyword;
//import com.bestarmedia.migration.model.mongo.VideoFile;
//import com.bestarmedia.migration.model.mongo.vod.*;
//import com.bestarmedia.migration.model.mysql.Language;
//import com.bestarmedia.migration.model.mysql.Musician;
//import com.bestarmedia.migration.model.mysql.Part;
//import com.bestarmedia.migration.model.mysql.Song;
//import com.bestarmedia.migration.repository.mongo.vod.*;
//import com.bestarmedia.migration.repository.mysql.*;
//import com.fasterxml.jackson.core.type.TypeReference;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.*;
//
//@Service
//@Slf4j
//public class MigrateMySQL2MongoVodService extends MigrateBase {
//
//    private final MysqlMusicianRepository mysqlMusicianRepository;
//    private final MysqlPartRepository mysqlPartRepository;
//    private final MysqlSongRepository mysqlSongRepository;
//    private final MysqlSongTypeRepository mysqlSongTypeRepository;
//    private final MysqlLanguageRepository mysqlLanguageRepository;
//
//    private final VodSingerRepository vodSingerRepository;
//    private final VodSongVersionRepository vodSongVersionRepository;
//    private final VodSongRepository vodSongRepository;
//    private final VodAreaRepository vodAreaRepository;
//    private final VodLanguageRepository vodLanguageRepository;
//    private final VodSongTypeRepository vodSongTypeRepository;
//
//    private int versionCount = 0;
//    private int songCount = 0;
//
//    private int musicianCount = 0;
//    private int musicianMergeCount = 0;
//    private int musicianGiveUpCount = 0;
//
//    @Autowired
//    public MigrateMySQL2MongoVodService(MysqlMusicianRepository mysqlMusicianRepository,
//                                        MysqlPartRepository mysqlPartRepository,
//                                        MysqlSongRepository mysqlSongRepository,
//                                        MysqlSongTypeRepository mysqlSongTypeRepository,
//                                        MysqlLanguageRepository mysqlLanguageRepository,
//
//                                        VodSingerRepository vodSingerRepository,
//                                        VodSongVersionRepository vodSongVersionRepository,
//                                        VodSongRepository vodSongRepository,
//                                        VodAreaRepository vodAreaRepository,
//                                        VodLanguageRepository vodLanguageRepository,
//                                        VodSongTypeRepository vodSongTypeRepository
//    ) {
//        this.mysqlMusicianRepository = mysqlMusicianRepository;
//        this.mysqlPartRepository = mysqlPartRepository;
//        this.mysqlSongRepository = mysqlSongRepository;
//        this.mysqlSongTypeRepository = mysqlSongTypeRepository;
//        this.mysqlLanguageRepository = mysqlLanguageRepository;
//
//        this.vodSingerRepository = vodSingerRepository;
//        this.vodSongVersionRepository = vodSongVersionRepository;
//        this.vodSongRepository = vodSongRepository;
//        this.vodAreaRepository = vodAreaRepository;
//        this.vodLanguageRepository = vodLanguageRepository;
//        this.vodSongTypeRepository = vodSongTypeRepository;
//    }
//
//    public void migrate() {
//        long currentTimeMillis = System.currentTimeMillis();
//        mergeLanguage();
//        mergePart();
//        mergeSongType();
//        migrateMusician();
//        migrateSong();
//        System.out.println("数据处理总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒");
//    }
//
//
//    public void mergePart() {
//        List<Part> parts = mysqlPartRepository.findAll();
//        long delCount = vodAreaRepository.cleanAllData();
//        System.out.println("清除地区信息数量量:" + delCount);
//        parts.forEach(item -> {
//            try {
//                VodArea area = new VodArea();
//                area.setCode(item.getId());
//                area.setName(item.getName());
//                area.setParentCode(0);
//                area.setIsShow(item.getIsShow());
//                area.setSort(item.getSort());
//                area.setCreatedAt(item.getCreatedAt());
//                area.setUpdatedAt(item.getUpdatedAt());
//                area.setDeletedAt(item.getDeletedAt());
//                VodArea save = vodAreaRepository.insert(area);
//                System.out.println("保存地区信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//
//    public void mergeLanguage() {
//        List<Language> languages = mysqlLanguageRepository.findAll();
//        long delCount = vodLanguageRepository.cleanAllData();
//        System.out.println("清除语种信息数量量:" + delCount);
//        languages.forEach(item -> {
//            try {
//                VodLanguage language = new VodLanguage();
//                language.setCode(item.getId());
//                language.setName(item.getLanguageName());
//                language.setParentCode(0);
//                language.setIsShow(item.getIsShow());
//                language.setSort(item.getSort());
//                language.setCreatedAt(item.getCreatedAt());
//                language.setUpdatedAt(item.getUpdatedAt());
//                language.setDeletedAt(item.getDeletedAt());
//                VodLanguage save = vodLanguageRepository.insert(language);
//                System.out.println("保存语种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public void mergeSongType() {
//        List<com.bestarmedia.migration.model.mysql.SongType> songTypes = mysqlSongTypeRepository.findAll();
//        long delCount = vodSongTypeRepository.cleanAllData();
//        System.out.println("清除曲种信息数量量:" + delCount);
//        songTypes.forEach(item -> {
//            try {
//                VodSongType songType = new VodSongType();
//                songType.setCode(item.getId());
//                songType.setName(item.getName());
//                songType.setParentCode(item.getParentId());
//                songType.setIsShow(item.getIsShow());
//                songType.setSort(item.getSort());
//                songType.setCreatedAt(item.getCreatedAt());
//                songType.setUpdatedAt(item.getUpdatedAt());
//                songType.setDeletedAt(item.getDeletedAt());
//                VodSongType save = vodSongTypeRepository.insert(songType);
//                System.out.println("保存曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//
//    public String migrateMusician() {
//        long delCount = vodSingerRepository.cleanAllData();
//        System.out.println("清除音乐人信息数量量:" + delCount);
//        musicianCount = 0;
//        musicianMergeCount = 0;
//        musicianGiveUpCount = 0;
//        long cur = System.currentTimeMillis();
//        int count = mysqlMusicianRepository.getMusicianSameNameCount();
//        final int pageSize = 10000;
//        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
////        size = 1;
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
//        for (int i = 0; i < size; i++) {
//            migrateMusician(i * pageSize, pageSize);
//        }
//        String text = "MySQL音乐人总量" + count + " 合并同名迁移音乐人总数：" + musicianCount + " 合并音乐人数量：" + musicianMergeCount
//                + " 丢弃音乐人数量：" + musicianGiveUpCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
//        return text;
//    }
//
//    private VodSinger createVodSinger(Musician item) {
//        VodSinger singer = new VodSinger();
//        singer.setCode(item.getMusicianId());
//        singer.setMusicianName(CommonUtil.deleteSpaceAndUpperFirst(item.getMusicianName()));
//        singer.setSimplicity(StringUtils.isEmpty(item.getMusicianInitial()) ? "" : item.getMusicianInitial().toUpperCase());
//        singer.setWordCount(item.getWordCount());
//
//        List<Integer> musicianTypes = new ArrayList<>();
//        List<String> musicianTypeTexts = new ArrayList<>();
//        if (item.getIsSinger() == 1) {
//            musicianTypes.add(1);
//            musicianTypeTexts.add("歌手");
//        }
//        if (item.getIsComposer() == 1) {
//            musicianTypes.add(2);
//            musicianTypeTexts.add("曲作者");
//        }
//        if (item.getIsLyricist() == 1) {
//            musicianTypes.add(3);
//            musicianTypeTexts.add("词作者");
//        }
//        if (item.getIsLitigant() == 1) {
//            musicianTypes.add(4);
//            musicianTypeTexts.add("诉讼权利人");
//        }
//        if (item.getIsProducer() == 1) {
//            musicianTypes.add(5);
//            musicianTypeTexts.add("制作权利人");
//        }
//        if (item.getIsPublisher() == 1) {
//            musicianTypes.add(6);
//            musicianTypeTexts.add("出品权利人");
//        }
//        singer.setMusicianType(musicianTypes);
////                    singer.setMusicianTypeText(musicianTypeTexts);
//        singer.setRole(item.getRole());
//        singer.setSex(item.getSex());
//        singer.setHot(item.getHot().longValue());
//        singer.setBirthday(item.getBirthday());
//        singer.setPart(null);
//        Optional<Part> part;
//        if (item.getPart() > 0 && (part = this.mysqlPartRepository.findById(item.getPart())).isPresent()) {
//            singer.setPart(new CodeName(item.getPart(), part.get().getName()));
//        }
//
//        singer.setImgFilePath(item.getImgFilePath());
//        singer.setStatus(item.getStatus());
//
//        List<String> alias = new ArrayList<>();
//        //合并到曾用名
//        if (!StringUtils.isEmpty(item.getSimpBynameOne())) {//别名1
//            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameOne()));
//        } else if (!StringUtils.isEmpty(item.getSimpBynameTwo())) {//别名2
//            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameTwo()));
//        } else if (!StringUtils.isEmpty(item.getSimpBynameThree())) {//别名3
//            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameThree()));
//        } else if (!StringUtils.isEmpty(item.getSimpBynameFour())) {//别名4
//            alias.add(CommonUtil.deleteSpaceAndUpperFirst(item.getSimpBynameFour()));
//        }
//        singer.setAlias(alias);//别名
//
//        List<SearchKeyword> searchKeywords = new ArrayList<>();
//        SearchKeyword searchKeyword = new SearchKeyword();
//        searchKeyword.setType(0);
//        searchKeyword.setCode(singer.getCode());
//        List<String> keywords = new ArrayList<>();
//        keywords.add(singer.getMusicianName());
//        keywords.add(singer.getSimplicity());
//        if (!alias.isEmpty()) {
//            keywords.addAll(alias);
//        }
//        searchKeyword.setKeywords(keywords);
//        searchKeywords.add(searchKeyword);
//
//        singer.setSearchKeywords(searchKeywords);
//
//        singer.setCreatedAt(item.getCreatedAt());
//        singer.setUpdatedAt(item.getUpdatedAt());
//        singer.setDeletedAt(item.getDeletedAt());
//
//        return singer;
//    }
//
//
//    private void migrateMusician(int index, int pageSize) {
//        try {
//            List<Musician> list = mysqlMusicianRepository.getMusicianSameName(index, pageSize);
//            Map<String, Musician> map = new HashMap<>();
//            list.forEach(item -> {
//                String musicianName = CommonUtil.deleteSpaceAndUpperFirst(item.getMusicianName());
//                item.setMusicianName(musicianName);
//                if (map.containsKey(musicianName)) {
//                    musicianMergeCount++;
//                    Musician musician = map.get(musicianName);
//                    if ((musician.getStatus() != 1 && item.getStatus() == 1)
//                            || (StringUtils.isEmpty(musician.getMusicianInitial()) && !StringUtils.isEmpty(item.getMusicianInitial()))
//                            || (musician.getWordCount() < 1 && item.getWordCount() > 0)
//                            || (musician.getHot() < item.getHot())
//                            || (StringUtils.isEmpty(musician.getImgFilePath()) && !StringUtils.isEmpty(item.getImgFilePath()))) {
//
//                        //补充信息
//                        if (item.getPart() == 0 && musician.getPart() != 0) {
//                            item.setPart(musician.getPart());
//                        }
//                        if (item.getIsSinger() == 0 && musician.getIsSinger() != 0) {
//                            item.setIsSinger(musician.getIsSinger());
//                        }
//                        if (item.getIsLyricist() == 0 && musician.getIsLyricist() != 0) {
//                            item.setIsLyricist(musician.getIsLyricist());
//                        }
//                        if (item.getIsComposer() == 0 && musician.getIsComposer() != 0) {
//                            item.setIsComposer(musician.getIsComposer());
//                        }
//                        if (item.getIsLitigant() == 0 && musician.getIsLitigant() != 0) {
//                            item.setIsLitigant(musician.getIsLitigant());
//                        }
//                        if (item.getIsProducer() == 0 && musician.getIsProducer() != 0) {
//                            item.setIsProducer(musician.getIsProducer());
//                        }
//                        if (item.getIsPublisher() == 0 && musician.getIsPublisher() != 0) {
//                            item.setIsPublisher(musician.getIsPublisher());
//                        }
//                        map.put(musicianName, item);
//                        try {
//                            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 合并音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        try {
//                            musicianGiveUpCount++;
//                            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 丢弃音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else {
//                    map.put(musicianName, item);
//                }
//            });
//            for (Map.Entry<String, Musician> entry : map.entrySet()) {
//                Musician musician = entry.getValue();
//                VodSinger save = vodSingerRepository.insert(createVodSinger(musician));
//                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存音乐人信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//                musicianCount++;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public String migrateSong() {
//        System.out.println("清除版本信息数量:" + vodSongVersionRepository.cleanAllData());
//        System.out.println("清除歌曲信息数量:" + vodSongRepository.cleanAllData());
//        versionCount = 0;
//        songCount = 0;
//        long cur = System.currentTimeMillis();
//        int count = mysqlSongRepository.getSongSameNameCount();
//        final int pageSize = 10000;
//        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
//        for (int i = 0; i < size; i++) {
//            migrateSong(i * pageSize, pageSize);
//        }
//        String text = "处理歌曲耗时：" + ((System.currentTimeMillis() - cur) / 1000)
//                + "秒  版本数量：" + versionCount + " 合并后歌曲总数：" + songCount;
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
//        return text;
//    }
//
//    public void migrateSong(int index, int pageSize) {
//        try {
//            long cur = System.currentTimeMillis();
//            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 开始处理歌曲index：" + index);
//            Map<String, VodSong> map = new HashMap<>();
//            List<Song> list = mysqlSongRepository.getSongSameName(index, pageSize);
//            list.forEach(item -> {
//                try {
//                    String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(item.getSongName()));
//                    String key = (songName + ";" + item.getSongId()).toUpperCase();
//                    if (!(StringUtils.isEmpty(item.getSinger()) || "[]".equals(item.getSinger()) || "[\"\"]".equals(item.getSinger()) || "[\" \"]".equals(item.getSinger()))) {
//                        List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(item.getSinger(), new TypeReference<List<String>>() {
//                        });
//                        singerNames.sort(Comparator.naturalOrder());
//                        key = (songName + ";" + CommonUtil.OBJECT_MAPPER.writeValueAsString(singerNames)).toUpperCase();
//                    }
//                    if (map.containsKey(key)) {
//                        VodSong vodSong = map.get(key);
//                        createVersion(vodSong.getCode(), item);
//                        vodSong.setHot(0L);
//                        vodSong.setHotSum(vodSong.getHotSum() + item.getHot());
//
//                        if ((vodSong.getLyricist() == null || vodSong.getLyricist().isEmpty()) && (!StringUtils.isEmpty(item.getLyricistMid()) || !StringUtils.isEmpty(item.getLyricist()))) {//词作者
//                            vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
//                            System.out.println("补充词作者：" + item.getId() + "  " + item.getLyricistMid() + " " + item.getLyricist());
//                        }
//                        if ((vodSong.getComposer() == null || vodSong.getComposer().isEmpty()) && (!StringUtils.isEmpty(item.getComposerMid()) || !StringUtils.isEmpty(item.getComposer()))) {//曲作者
//                            vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
//                            System.out.println("补充曲作者：" + item.getId() + "  " + item.getComposerMid() + "  " + item.getComposer());
//                        }
//                        if (StringUtils.isEmpty(vodSong.getSongInitial()) && !StringUtils.isEmpty(item.getSongInitial())) {
//                            vodSong.setSongInitial(item.getSongInitial());
//                            System.out.println("补充简拼：" + item.getId() + "  " + item.getSongInitial());
//                        }
//                        if ((vodSong.getWordCount() == null || vodSong.getWordCount() == 0) && (item.getWordCount() != null && item.getWordCount() > 0)) {
//                            vodSong.setWordCount(item.getWordCount());
//                            System.out.println("补充字数：" + item.getId() + "  " + item.getWordCount());
//                        }
//                        if ((vodSong.getSongType() == null || vodSong.getSongType().getCode() == 0) && (item.getSongType() != null)) {
//                            vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
//                            System.out.println("补充歌曲类型：" + item.getId() + "  " + item.getSongType().getName());
//                        }
//                        if ((vodSong.getLanguage() == null || vodSong.getLanguage().getCode() == 0) && (item.getLanguage() != null)) {
//                            vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
//                            System.out.println("补充语种：" + item.getId() + "  " + item.getLanguage().getLanguageName());
//                        }
//                        if ((vodSong.getRecommend() == null || vodSong.getRecommend() == 0) && (item.getSofthard() != null && item.getSofthard() > 0)) {
//                            vodSong.setRecommend(item.getSofthard());
//                            System.out.println("补充推荐歌曲：" + item.getId() + "  " + item.getSofthard());
//                        }
//                        if ((vodSong.getStatus() == null || vodSong.getStatus() == 0) && (item.getStatus() != null && item.getStatus() > 0)) {
//                            vodSong.setStatus(item.getStatus());
//                            System.out.println("补充上架状态：" + item.getId() + "  " + item.getStatus());
//                        }
//                        map.put(key, vodSong);
//
//                    } else {
//                        VodSong vodSong = new VodSong();
//                        vodSong.setCode(item.getSongId());
//                        vodSong.setSongInitial(StringUtils.isEmpty(item.getSongInitial()) ? "" : item.getSongInitial().toUpperCase());
//                        vodSong.setSongName(songName);
//                        vodSong.setSinger(getIdNames(item.getSingerMid(), item.getSinger()));
//                        vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
//                        vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
//                        vodSong.setWordCount(item.getWordCount());
//                        vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
//                        vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
//                        vodSong.setTag(null);
//                        vodSong.setRecommend(item.getSofthard());
//                        vodSong.setHot(0L);
//                        vodSong.setHot(item.getHot());
//                        vodSong.setHotSum(item.getHot());
//                        vodSong.setStatus(item.getStatus());
//                        vodSong.setCreatedAt(item.getCreatedAt());
//                        vodSong.setUpdatedAt(item.getUpdatedAt());
//
//                        createVersion(vodSong.getCode(), item);
//
//                        map.put(key, vodSong);
//                    }
//                } catch (Exception e) {
//                   e.printStackTrace();
//                }
//            });
//
//            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 处理数据耗时：" + (System.currentTimeMillis() - cur));
//            cur = System.currentTimeMillis();
//            for (Map.Entry<String, VodSong> entry : map.entrySet()) {
//                VodSong save = vodSongRepository.insert(entry.getValue());
//                songCount++;
//                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存歌曲信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//            }
//            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 存储数据耗时：" + (System.currentTimeMillis() - cur));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private VodSongVersion createVersion(Integer songId, Song song) {
//        VodSongVersion version = new VodSongVersion();
//        version.setCode(song.getSongId());//以旧歌曲版本命名版本id
//        version.setSongCode(songId);
//        version.setSongCodeOld(song.getSongId());
//        CodeName edition = getEdition(song.getVideoType());
//        version.setType(1);
//        version.setVersionsType(edition.getCode());
//        version.setVersionsTypeName(new VodVersionTypeName(edition.getCode(),edition.getName(),1,0));
//        version.setSource(song.getSongSource() + "");
//        version.setAlbum(null);
////        version.setLitigant(getIdNames(song.getLitigantMid(), song.getLitigant()));//诉讼权利人
////        version.setProducer(getIdNames(song.getProducerMid(), song.getProducer()));//制作权利人
////        version.setPublisher(getIdNames(song.getPublisherMid(), song.getPublisher()));//出品权利人
//        version.setIncreaseHot(song.getHot());
//        version.setVersionHot(0L);
//        version.setVersionHotSum(song.getHot());
//        version.setIssueTime(null);
//        version.setRecommend(song.getSofthard());
//        version.setStatus(song.getStatus());
//        version.setCreatedAt(song.getCreatedAt());
//        version.setUpdatedAt(song.getUpdatedAt());
//        version.setDeletedAt(song.getDeletedAt());
//        List<VideoFile> files = new ArrayList<>();
//        files.add(createFileDto(song));
//        version.setVideoFileList(files);
//        VodSongVersion save = vodSongVersionRepository.insert(version);
//        try {
//            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存版本信息到mongodb："
//                    + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        versionCount++;
//        return save;
//    }
//}
