package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.song.SongInformation;
import com.bestarmedia.migration.model.mongo.song.SongMusician;
import com.bestarmedia.migration.model.mongo.song.SongSongVersion;
import com.bestarmedia.migration.model.mongo.vod.VodSinger;
import com.bestarmedia.migration.model.mongo.vod.VodSong;
import com.bestarmedia.migration.model.mongo.vod.VodSongVersion;
import com.bestarmedia.migration.model.mongo.vod.VodSongVideoFile;
import com.bestarmedia.migration.model.mysql.Musician;
import com.bestarmedia.migration.model.mysql.Part;
import com.bestarmedia.migration.model.mysql.Song;
import com.bestarmedia.migration.repository.mongo.song.SongInformationRepository;
import com.bestarmedia.migration.repository.mongo.song.SongMusicianRepository;
import com.bestarmedia.migration.repository.mongo.song.SongSongVersionRepository;
import com.bestarmedia.migration.repository.mongo.vod.VodSingerRepository;
import com.bestarmedia.migration.repository.mongo.vod.VodSongRepository;
import com.bestarmedia.migration.repository.mongo.vod.VodSongVersionRepository;
import com.bestarmedia.migration.repository.mysql.MysqlMusicianRepository;
import com.bestarmedia.migration.repository.mysql.MysqlPartRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class MigrateService {

    private final MysqlMusicianRepository mysqlMusicianRepository;
    private final MysqlPartRepository mysqlPartRepository;
    private final MysqlSongRepository mysqlSongRepository;


    private final VodSingerRepository vodSingerRepository;
    private final VodSongVersionRepository vodSongVersionRepository;
    private final VodSongRepository vodSongRepository;

    private final SongMusicianRepository songMusicianRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongInformationRepository songInformationRepository;

    private int versionCount = 0;
    private int songCount = 0;

    private int musicianCount = 0;
    private int musicianMergeCount = 0;
    private int musicianGiveUpCount = 0;

    @Autowired
    public MigrateService(MysqlMusicianRepository mysqlMusicianRepository,
                          MysqlPartRepository mysqlPartRepository,
                          MysqlSongRepository mysqlSongRepository,

                          VodSingerRepository vodSingerRepository,
                          VodSongVersionRepository vodSongVersionRepository,
                          VodSongRepository vodSongRepository,

                          SongMusicianRepository songMusicianRepository,
                          SongSongVersionRepository songSongVersionRepository,
                          SongInformationRepository songInformationRepository
    ) {
        this.mysqlMusicianRepository = mysqlMusicianRepository;
        this.mysqlPartRepository = mysqlPartRepository;
        this.mysqlSongRepository = mysqlSongRepository;

        this.vodSingerRepository = vodSingerRepository;
        this.vodSongVersionRepository = vodSongVersionRepository;
        this.vodSongRepository = vodSongRepository;

        this.songMusicianRepository = songMusicianRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songInformationRepository = songInformationRepository;
    }

//    private static String deleteSpecialChar(String text) {
//        return text.replace("\"", "").replace("“", "").replace("”", "")
//                .replace("/", "|").replace("｜", "|").replace("，", "|").replace("&", "|");
//    }
//
//    public MongoTaskData getTaskData() {
//        MongoTaskData mongoTaskData = null;
//        Object object = redisTemplate.opsForList().rightPop(mongoTaskDataKey, Duration.ofSeconds(2));
//        if (object != null) {
//            try {
//                mongoTaskData = CommonUtil.objectMapper.readValue(object.toString(), MongoTaskData.class);
//                switch (mongoTaskData.type) {//1歌曲，2音乐人，3歌曲分类，4语种，5地区
//                    case 1:
//                        List<MongoSongDTO> songs = mongoTaskData.data;
//                        System.out.println("队列歌曲数量：" + songs.size());
//                        break;
//                    case 2:
//                        List<MongoMusicianDTO> musicians = mongoTaskData.data;
//                        System.out.println("队列歌星数量：" + musicians.size());
//                        break;
//                    case 3:
//                        List<MongoSongTypeDTO> songTypeDTOS = mongoTaskData.data;
//                        System.out.println("队列歌曲类型数量：" + songTypeDTOS.size());
//                        break;
//                    case 4:
//                        List<MongoMusicianDTO> languages = mongoTaskData.data;
//                        System.out.println("队列语种数量：" + languages.size());
//                        break;
//                    case 5:
//                        List<MongoMusicianDTO> parts = mongoTaskData.data;
//                        System.out.println("队列地区数量：" + parts.size());
//                        break;
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return mongoTaskData;
//    }
//
//    public void deleteTaskQueue() {
//        if (redisTemplate.hasKey(mongoTaskDataKey)) {
//            redisTemplate.delete(mongoTaskDataKey);
//        }
//    }

    public void migrate(int database) {
        long currentTimeMillis = System.currentTimeMillis();
//        deleteTaskQueue();
//        mergeLanguage();
//        mergePart();
//        mergeSongType();
        migrateMusician(database);
        migrateSong(database);
        System.out.println("数据处理总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒");
    }

    public int getSingerCountByName() {
        return mysqlMusicianRepository.getMusicianSameNameCount();
    }

    public String getSingerName(String singerName) {
        return vodSingerRepository.findSingerByName(singerName).getMusicianName();
    }


//    public void mergePart() {
//        List<Part> parts = partRepository.findAll();
//        List<MongoSongTypeDTO> partsDTO = new ArrayList<>();
//        parts.forEach(item -> {
//            MongoSongTypeDTO dto = new MongoSongTypeDTO();
//            dto.code = (item.getId());
//            dto.parent_code = (0);
//            dto.name = (item.getName());
//            dto.sort = (item.getSort());
//            dto.is_show = (item.getIsShow());
//            dto.created_at = (item.getCreatedAt());
//            dto.updated_at = (item.getUpdatedAt());
//            partsDTO.add(dto);
//        });
//        try {
//            MongoTaskData mongoTaskData = new MongoTaskData(5, partsDTO);
////            redisTemplate.opsForList().leftPush(mongoTaskDataKey, CommonUtil.objectMapper.writeValueAsString(mongoTaskData));
//            String json = CommonUtil.objectMapper.writeValueAsString(mongoTaskData);
//            System.out.println("地区数据:" + json);
//            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTINGKEY, json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


//    public void mergeLanguage() {
//        List<Language> languages = languageRepository.findAll();
//        List<MongoSongTypeDTO> languagesDTO = new ArrayList<>();
//        languages.forEach(item -> {
//            MongoSongTypeDTO dto = new MongoSongTypeDTO();
//            dto.code = (item.getId());
//            dto.parent_code = (0);
//            dto.name = (item.getLanguageName());
//            dto.sort = (item.getSort());
//            dto.is_show = (item.getIsShow());
//            dto.created_at = (item.getCreatedAt());
//            dto.updated_at = (item.getUpdatedAt());
//            languagesDTO.add(dto);
//        });
//        try {
//            MongoTaskData mongoTaskData = new MongoTaskData(4, languagesDTO);
////            redisTemplate.opsForList().leftPush(mongoTaskDataKey, CommonUtil.objectMapper.writeValueAsString(mongoTaskData));
//            String json = CommonUtil.objectMapper.writeValueAsString(mongoTaskData);
//            System.out.println("语种数据:" + json);
//            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTINGKEY, json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    public void mergeSongType() {
//        List<SongType> songTypes = songTypeRepository.findAll();
//        List<MongoSongTypeDTO> songTypeDTOS = new ArrayList<>();
//        songTypes.forEach(item -> {
//            MongoSongTypeDTO dto = new MongoSongTypeDTO();
//            dto.code = (item.getId());
//            dto.parent_code = (item.getParentId());
//            dto.name = (item.getName());
//            dto.sort = (item.getSort());
//            dto.is_show = (item.getIsShow());
//            dto.created_at = (item.getCreatedAt());
//            dto.updated_at = (item.getUpdatedAt());
//            songTypeDTOS.add(dto);
//        });
//        try {
//            MongoTaskData mongoTaskData = new MongoTaskData(3, songTypeDTOS);
////            redisTemplate.opsForList().leftPush(mongoTaskDataKey, CommonUtil.objectMapper.writeValueAsString(mongoTaskData));
//            String json = CommonUtil.objectMapper.writeValueAsString(mongoTaskData);
//            System.out.println("歌曲类型数据:" + json);
//            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTINGKEY, json);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public String migrateMusician(int database) {
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
            migrateMusician(i * pageSize, pageSize, database);
        }
        String text = "MySQL音乐人总量" + count + " 合并同名迁移音乐人总数：" + musicianCount + " 合并音乐人数量：" + musicianMergeCount
                + " 丢弃音乐人数量：" + musicianGiveUpCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }

    private VodSinger createVodSinger(Musician item) {
        VodSinger singer = new VodSinger();
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
        singer.setHot(item.getHot());
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
        singer.setCreatedAt(item.getCreatedAt());
        singer.setUpdatedAt(item.getUpdatedAt());
        singer.setDeletedAt(item.getDeletedAt());

        return singer;
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
        singer.setHotSum(Long.valueOf(item.getHot()));
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
        singer.setMold(1);
        singer.setRemark("");
        singer.setCreatedAt(item.getCreatedAt());
        singer.setUpdatedAt(item.getUpdatedAt());
        singer.setDeletedAt(item.getDeletedAt());
        singer.setCreateUser(item.getCreateUser());
        singer.setUpdateUser(item.getUpdateUser());

        return singer;
    }


    private void migrateMusician(int index, int pageSize, int database) {
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
                if (database == 1) {
                    SongMusician save = songMusicianRepository.replace(createSongMusician(musician));
                    System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存音乐人信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                } else {
                    VodSinger save = vodSingerRepository.insert(createVodSinger(musician));
                    System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存音乐人信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                }
                musicianCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void mergeSongIndex() {
//        long cur = System.currentTimeMillis();
//        final int pageSize = 10;
//        int size = 2;
//        for (int i = 0; i < size; i++) {
//            mergeSong(i * pageSize, pageSize);
//        }
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000)
//                + "秒  多版本歌曲数量：" + mergeCount + " 其中大于3个版本数量：" + mergeCount3 + " 合并后歌曲总数：" + totalSongCount);
//    }

    public String migrateSong(int database) {
        versionCount = 0;
        songCount = 0;
        long cur = System.currentTimeMillis();
        int count = mysqlSongRepository.getSongSameNameCount();
        final int pageSize = 10000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = 0; i < size; i++) {
            if (database == 1) {
                migrateSong2CmsV7(i * pageSize, pageSize);
            } else {
                migrateSong(i * pageSize, pageSize);
            }
        }
        String text = "处理歌曲耗时：" + ((System.currentTimeMillis() - cur) / 1000)
                + "秒  版本数量：" + versionCount + " 合并后歌曲总数：" + songCount;
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }

    //
    public void migrateSong(int index, int pageSize) {
        try {
            long cur = System.currentTimeMillis();
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 开始处理歌曲index：" + index);
            Map<String, VodSong> map = new HashMap<>();
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
                        VodSong vodSong = map.get(key);
                        createVersion(vodSong.getCode(), item);
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
                            vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
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
                        map.put(key, vodSong);

                    } else {
                        VodSong vodSong = new VodSong();
                        vodSong.setCode(item.getSongId());
                        vodSong.setSongInitial(StringUtils.isEmpty(item.getSongInitial()) ? "" : item.getSongInitial().toUpperCase());
                        vodSong.setSongName(songName);
                        vodSong.setSinger(getIdNames(item.getSingerMid(), item.getSinger()));
                        vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
                        vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
                        vodSong.setWordCount(item.getWordCount());
                        vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
                        vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
                        vodSong.setTag(null);
                        vodSong.setRecommend(item.getSofthard());
                        vodSong.setHot(0L);
                        vodSong.setHot(item.getHot());
                        vodSong.setHotSum(item.getHot());
                        vodSong.setStatus(item.getStatus());
                        vodSong.setCreatedAt(item.getCreatedAt());
                        vodSong.setUpdatedAt(item.getUpdatedAt());

                        createVersion(vodSong.getCode(), item);

                        map.put(key, vodSong);
                    }
                } catch (Exception e) {
                    new RuntimeException();
                }
            });

            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 处理数据耗时：" + (System.currentTimeMillis() - cur));
            cur = System.currentTimeMillis();
            for (Map.Entry<String, VodSong> entry : map.entrySet()) {
                VodSong save = vodSongRepository.insert(entry.getValue());
                songCount++;
                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存歌曲信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            }
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 存储数据耗时：" + (System.currentTimeMillis() - cur));
        } catch (Exception e) {
            new RuntimeException();
        }
    }

    private CodeName getEdition(Integer videoType) {
        switch (videoType) {
            case 1:
                return new CodeName(1, "MV");
            case 2:
                return new CodeName(3, "现场");
            case 3:
                return new CodeName(4, "综艺");
            case 4:
                return new CodeName(5, "尊享");
            case 7:
            case 8:
            case 9:
            case 10:
                return new CodeName(2, "重制");
            default:
                return new CodeName(6, "其他");
        }
    }

    private VodSongVersion createVersion(Integer songId, Song song) {
        VodSongVersion version = new VodSongVersion();
        version.setCode(song.getSongId() + "");//以旧歌曲版本命名版本id
        version.setSongCode(songId);
        version.setSongCodeOld(song.getSongId());
        CodeName edition = getEdition(song.getVideoType());
        version.setVersionNameCode(edition.getCode());
//        version.setVersionName( edition.getName());
        version.setVersionsType(1);
        version.setSource(song.getSongSource() + "");
        version.setAlbum(null);
//        version.setLitigant(getIdNames(song.getLitigantMid(), song.getLitigant()));//诉讼权利人
//        version.setProducer(getIdNames(song.getProducerMid(), song.getProducer()));//制作权利人
//        version.setPublisher(getIdNames(song.getPublisherMid(), song.getPublisher()));//出品权利人
        version.setIncreaseHot(song.getHot());
        version.setVersionHot(0L);
        version.setVersionHotSum(song.getHot());
        version.setIssueTime(null);
        version.setRecommend(song.getSofthard());
        version.setStatus(song.getStatus());
        version.setCreatedAt(song.getCreatedAt());
        version.setUpdatedAt(song.getUpdatedAt());
        version.setDeletedAt(song.getDeletedAt());
        List<VodSongVideoFile> files = new ArrayList<>();
        files.add(createFileDto(song));
        version.setVideoFileList(files);
        VodSongVersion save = vodSongVersionRepository.insert(version);
        try {
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存版本信息到mongodb："
                    + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
        } catch (Exception e) {
            e.printStackTrace();
        }
        versionCount++;
        return save;
    }


    public void migrateSong2CmsV7(int index, int pageSize) {
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
                            vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
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
                        if (StringUtils.isEmpty(vodSong.getNoteOne()) && !StringUtils.isEmpty(item.getNoteOne())) {
                            vodSong.setNoteOne(item.getNoteOne());
                            System.out.println("补充备注1：" + item.getId() + "  " + item.getNoteOne());
                        }
                        if (StringUtils.isEmpty(vodSong.getNoteTwo()) && !StringUtils.isEmpty(item.getNoteTwo())) {
                            vodSong.setNoteTwo(item.getNoteTwo());
                            System.out.println("补充备注2：" + item.getId() + "  " + item.getNoteTwo());
                        }
                        if (StringUtils.isEmpty(vodSong.getNoteThree()) && !StringUtils.isEmpty(item.getNoteThree())) {
                            vodSong.setNoteThree(item.getNoteThree());
                            System.out.println("补充备注3：" + item.getId() + "  " + item.getNoteThree());
                        }
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
                        vodSong.setSongType(new CodeName(item.getSongType().getId(), item.getSongType().getName()));
                        vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
                        vodSong.setTag(null);
                        vodSong.setRecommend(item.getSofthard());
                        vodSong.setHot(0L);
                        vodSong.setHot(item.getHot());
                        vodSong.setHotSum(item.getHot());
                        vodSong.setStatus(item.getStatus());
                        vodSong.setNoteOne(item.getNoteOne());
                        vodSong.setNoteTwo(item.getNoteTwo());
                        vodSong.setNoteThree(item.getNoteThree());
                        vodSong.setCreatedAt(item.getCreatedAt());
                        vodSong.setUpdatedAt(item.getUpdatedAt());
                        vodSong.setCreateUser(item.getCreateUser());
                        vodSong.setUpdateUser(item.getUpdateUser());
                        createSongVersion(vodSong.getCode(), vodSong.getSongName(), item);
                        map.put(key, vodSong);
                    }
                } catch (Exception e) {
                    new RuntimeException();
                }
            });

            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 处理数据耗时：" + (System.currentTimeMillis() - cur));
            cur = System.currentTimeMillis();
            for (Map.Entry<String, SongInformation> entry : map.entrySet()) {
                SongInformation save = songInformationRepository.replace(entry.getValue());
                songCount++;
                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存歌曲信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            }
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 存储数据耗时：" + (System.currentTimeMillis() - cur));
        } catch (Exception e) {
            new RuntimeException();
        }
    }

    private SongSongVersion createSongVersion(Integer songId, String songName, Song song) {
        SongSongVersion version = new SongSongVersion();
        version.setCode(song.getSongId() + "");//以旧歌曲版本命名版本id
        version.setSongCode(songId);
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
        List<VodSongVideoFile> files = new ArrayList<>();
        files.add(createFileDto(song));
        version.setVideoFileList(files);
        SongSongVersion save = songSongVersionRepository.replace(version);
        try {
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存版本信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
        } catch (Exception e) {
            e.printStackTrace();
        }
        versionCount++;
        return save;
    }


    //
    public VodSongVideoFile createFileDto(Song song) {
        VodSongVideoFile file = new VodSongVideoFile();
        file.setCode(song.getSongCode() + "");
        file.setFileName(song.getMediaFilePath().substring(song.getMediaFilePath().lastIndexOf("/") + 1));
        file.setFilePath("https://song-enterprise.oss-cn-shenzhen.aliyuncs.com/song/h264/" + file.getFileName());
        file.setFormatName("H264");
        file.setVideoType(song.getVideoTypeDetail().getName());
        file.setResolutionWidth(song.getResolutionWidth());
        file.setResolutionHeight(song.getResolutionHeight());
        file.setAudioTrack(song.getAudioTrack());
        file.setVolume(song.getVolume());
        file.setHot(0);
        file.setStatus(song.getStatus());
        file.setLyricFilePath("");
        file.setScoreFilePath(!StringUtils.isEmpty(song.getScoreStandardFilePath()) && !StringUtils.isEmpty(song.getCoordinateFilePath()) ?
                (song.getScoreStandardFilePath() + "|" + song.getCoordinateFilePath()) : "");
        file.setRemark("");
        return file;
    }

    //
////    private IdNameDTO getLanguage(Integer languageId) {
////        Language language = languageRepository.findById(languageId).get();
////        return new IdNameDTO(language.getId(), language.getLanguageName());
////    }
////
////    private IdNameDTO getSongType(Integer songTypeId) {
////        SongType songType = songTypeRepository.findById(songTypeId).get();
////        return new IdNameDTO(songType.getId(), songType.getName());
////    }
//
//
    private static List<CodeName> getIdNames(String ids, String names) {
        List<CodeName> idNames = new ArrayList<>();
        List<Integer> singerIds = new ArrayList<>();
        List<String> singerNames = new ArrayList<>();
        try {
            singerIds = CommonUtil.OBJECT_MAPPER.readValue(ids, new TypeReference<List<Integer>>() {
            });
        } catch (Exception e) {
        }
        try {
            singerNames = CommonUtil.OBJECT_MAPPER.readValue(names, new TypeReference<List<String>>() {
            });

        } catch (Exception e) {
        }
        try {
            int size = singerIds.size() >= singerNames.size() ? singerIds.size() : singerNames.size();
            for (int i = 0; i < size; i++) {
                idNames.add(new CodeName(i < singerIds.size() ? singerIds.get(i) : 0, i < singerNames.size() ? CommonUtil.deleteStartEndSpaceChar(singerNames.get(i)) : ""));
            }
        } catch (Exception e) {

        }
        return idNames;
    }

//    public HashMap<String, List<Integer>> findSingerReplaceSongs(String singerName) {
//        List<Song> songs = vodSongRepository.findAll(getWhereClause(singerName));
//        HashMap<String, List<Integer>> map = new HashMap<>();
//
//        songs.forEach(item -> {
//            List<Song> ls = findBySongAndSingerName(item.getSongName(), singerName);
//            List<Integer> list = new ArrayList<>();
//            ls.forEach(song -> {
//                list.add(song.getSongId());
//            });
//            map.put(item.getSongId() + "", list);
//        });
//        return map;
//    }

//    private List<Song> findBySongAndSingerName(String songName, String singer) {
//        return vodSongRepository.findAll(getWhereClause2(songName, singer));
//    }

//    public Specification<Song> getWhereClause2(String songName, String singer) {
//        return (root, criteriaQuery, criteriaBuilder) -> {
//            Predicate predicate = criteriaBuilder.conjunction();
//            predicate.getExpressions().add(criteriaBuilder.notEqual(root.get("videoType"), 1));
//            if (!StringUtils.isEmpty(singer)) {
//                predicate.getExpressions().add(criteriaBuilder.or(criteriaBuilder.like(root.get("singer"), "%" + singer + "%")
//                ));
//            }
//            if (!StringUtils.isEmpty(songName)) {
//                predicate.getExpressions().add(criteriaBuilder.or(criteriaBuilder.like(root.get("songName"), songName)
//                ));
//            }
//            return predicate;
//        };
//    }

//    public Specification<Song> getWhereClause(String singer) {
//        return (root, criteriaQuery, criteriaBuilder) -> {
//            Predicate predicate = criteriaBuilder.conjunction();
//            predicate.getExpressions().add(criteriaBuilder.equal(root.get("status"), 1));
//            predicate.getExpressions().add(criteriaBuilder.equal(root.get("publish"), 1));//0 未发布 1 发布 2 冷冻
//            predicate.getExpressions().add(criteriaBuilder.equal(root.get("videoType"), 1));
//            predicate.getExpressions().add(criteriaBuilder.equal(root.get("songSource"), 0));
//            if (!StringUtils.isEmpty(singer)) {
//                predicate.getExpressions().add(criteriaBuilder.or(criteriaBuilder.like(root.get("singer"), "%" + singer + "%")
//                ));
//            }
//            return predicate;
//        };
//    }
}
