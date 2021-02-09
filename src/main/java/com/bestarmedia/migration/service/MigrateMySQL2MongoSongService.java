package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.*;
import com.bestarmedia.migration.model.mongo.song.*;
import com.bestarmedia.migration.model.mysql.*;
import com.bestarmedia.migration.repository.mongo.song.*;
import com.bestarmedia.migration.repository.mysql.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final SongAlbumRepository songAlbumRepository;
    private final SongTagRepository songTagRepository;
    private final SongUgcRepository songUgcRepository;

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
                                         SongAreaRepository songAreaRepository,
                                         SongAlbumRepository songAlbumRepository,
                                         SongTagRepository songTagRepository,
                                         SongUgcRepository songUgcRepository) {
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
        this.songAlbumRepository = songAlbumRepository;
        this.songTagRepository = songTagRepository;
        this.songUgcRepository = songUgcRepository;
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
                songArea.setIsShow(item.getDeletedAt() == null ? (item.getIsShow() + 1) : 0);
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
                language.setIsShow(item.getDeletedAt() == null ? (item.getIsShow() + 1) : 0);
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
                songType.setIsShow(item.getDeletedAt() == null ? (item.getIsShow() + 1) : 0);
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
        singer.setHotSum(item.getHot());
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
                        //补充信息
                        if (item.getPart() == 0 && musician.getPart() != 0) {
                            item.setPart(musician.getPart());
                        }
                        if (item.getIsSinger() == 0 && musician.getIsSinger() != 0) {
                            item.setIsSinger(musician.getIsSinger());
                        }
                        if (item.getIsLyricist() == 0 && musician.getIsLyricist() != 0) {
                            item.setIsLyricist(musician.getIsLyricist());
                        }
                        if (item.getIsComposer() == 0 && musician.getIsComposer() != 0) {
                            item.setIsComposer(musician.getIsComposer());
                        }
                        if (item.getIsLitigant() == 0 && musician.getIsLitigant() != 0) {
                            item.setIsLitigant(musician.getIsLitigant());
                        }
                        if (item.getIsProducer() == 0 && musician.getIsProducer() != 0) {
                            item.setIsProducer(musician.getIsProducer());
                        }
                        if (item.getIsPublisher() == 0 && musician.getIsPublisher() != 0) {
                            item.setIsPublisher(musician.getIsPublisher());
                        }
                        map.put(musicianName, item);
                        try {
                            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 合并音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item));
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
        System.out.println("清除专辑信息数量:" + songAlbumRepository.cleanAllData());
        System.out.println("清除标签信息数量:" + songTagRepository.cleanAllData());
        System.out.println("清除UGC信息数量:" + songUgcRepository.cleanAllData());
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

    private void insertToUgc(Song song) {
        SongUgc songUgc = new SongUgc();
        songUgc.setSongInitial(song.getSongInitial());
        songUgc.setSongName(song.getSongName());
        songUgc.setWordCount(song.getWordCount());
        songUgc.setSongType(createSongType(song));
        songUgc.setAudioTrack(song.getAudioTrack());
        if (!(StringUtils.isEmpty(song.getSinger()) || "[]".equals(song.getSinger()) || "[\"\"]".equals(song.getSinger()) || "[\" \"]".equals(song.getSinger()))) {
            songUgc.setSinger(getIdNames(song.getSingerMid(), song.getSinger()));
        }
        songUgc.setLanguage(new CodeName(song.getLanguage().getId(), song.getLanguage().getLanguageName()));
        songUgc.setStatus(song.getStatus());
        songUgc.setUgcOwner(new KtvUgcOwner(song.getCreateUser() + "", "", "", song.getKtvNetCode(), ""));
        songUgc.setFilePath(song.getMediaFilePath());
        songUgc.setResolutionWidth(song.getResolutionWidth());
        songUgc.setResolutionHeight(song.getResolutionHeight());
        songUgc.setAudioTrack(song.getAudioTrack());
        songUgc.setVolume(song.getVolume());
        songUgc.setCreateUser(song.getCreateUser());
        songUgc.setUpdateUser(song.getUpdateUser());
        songUgc.setCreatedAt(song.getCreatedAt());
        songUgc.setUpdatedAt(song.getUpdatedAt());
        songUgc.setDeletedAt(song.getDeletedAt());

        songUgcRepository.insert(songUgc);

    }

    public void migrateUgc() {
        long delCount = songUgcRepository.cleanAllData();
        System.out.println("清除音乐人信息数量量:" + delCount);
        migrateUgc2CmsV7();
    }

    private void migrateUgc2CmsV7() {
        List<Song> list = mysqlSongRepository.getUgc();
        list.forEach(item -> {
            if (!StringUtils.isEmpty(item.getKtvNetCode())) {//KTV定向歌曲
                insertToUgc(item);
            }
        });
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
                    if (!StringUtils.isEmpty(item.getKtvNetCode())) {//KTV定向歌曲
                        insertToUgc(item);
                    } else {
                        String key = (songName + ";" + item.getSongId()).toUpperCase();
                        if (!(StringUtils.isEmpty(item.getSinger()) || "[]".equals(item.getSinger()) || "[\"\"]".equals(item.getSinger()) || "[\" \"]".equals(item.getSinger()))) {
                            List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(item.getSinger(), new TypeReference<List<String>>() {
                            });
                            singerNames.sort(Comparator.naturalOrder());
                            key = (songName + ";" + CommonUtil.OBJECT_MAPPER.writeValueAsString(singerNames)).toUpperCase();
                        }
                        if (map.containsKey(key)) {
                            SongInformation vodSong = map.get(key);
                            createSongVersion(vodSong.getCode(), vodSong.getSongName(), item, 1, "", "", "");
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
                            if (vodSong.getWordCount() == 0 && item.getWordCount() > 0) {
                                vodSong.setWordCount(item.getWordCount());
                                System.out.println("补充字数：" + item.getId() + "  " + item.getWordCount());
                            }
                            if ((vodSong.getSongType() == null || vodSong.getSongType().getCode() == 0) && (item.getSongType() != null)) {
                                vodSong.setSongType(createSongType(item));
                                System.out.println("补充歌曲类型：" + item.getId() + "  " + item.getSongType().getName());
                            }
                            if ((vodSong.getLanguage() == null || vodSong.getLanguage().getCode() == 0) && (item.getLanguage() != null)) {
                                vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
                                System.out.println("补充语种：" + item.getId() + "  " + item.getLanguage().getLanguageName());
                            }
                            if (vodSong.getRecommend() == 0 && item.getSofthard() > 0) {
                                vodSong.setRecommend(item.getSofthard());
                                System.out.println("补充推荐歌曲：" + item.getId() + "  " + item.getSofthard());
                            }
                            if (vodSong.getStatus() == 0 && item.getStatus() > 0) {
                                vodSong.setStatus(item.getStatus());
                                System.out.println("补充上架状态：" + item.getId() + "  " + item.getStatus());
                            }
                            List<CodeNameParent> tagCodeNames = vodSong.getTag();
                            if (tagCodeNames == null) {
                                tagCodeNames = new ArrayList<>();
                            }
                            if (!StringUtils.isEmpty(item.getLyricFileMd5())) {
                                String[] tags = item.getLyricFileMd5().split("\\|");
                                for (String tag : tags) {
                                    if (tagCodeNames.isEmpty() || !isContainName(tagCodeNames, tag)) {
                                        SongTag songTag = songTagRepository.findByName(tag);
                                        if (songTag == null) {
                                            songTag = songTagRepository.insert(tag, songName, item.getSinger());
                                        }
                                        CodeNameParent codeNameParent = new CodeNameParent(0, "");
                                        codeNameParent.setCode(songTag.getCode());
                                        codeNameParent.setName(songTag.getTagName());
                                        tagCodeNames.add(codeNameParent);
                                    }
                                }
                            }
                            vodSong.setTag(tagCodeNames);
                            map.put(key, vodSong);

                        } else {
                            map.put(key, createSongInformation(item, songName, 1, "", "", ""));
                        }
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
//                System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 保存歌曲信息到mongodb：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            }
            System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总数：" + map.size() + " 存储数据耗时：" + (System.currentTimeMillis() - cur));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CodeNameParent createSongType(Song item) {
        CodeNameParent songType = new CodeNameParent();
        songType.setCode(item.getSongType().getId());
        songType.setName(item.getSongType().getName());
        songType.setParentCode(0);
        songType.setParentName("");
        Optional<com.bestarmedia.migration.model.mysql.SongType> optional;
        if (item.getSongType().getParentId() > 0 && (optional = this.mysqlSongTypeRepository.findById(item.getSongType().getParentId())).isPresent()) {
            songType.setParentCode(optional.get().getId());
            songType.setParentName(optional.get().getName());
        }
        return songType;
    }

    private SongInformation createSongInformation(Song item, String songName, int type, String original, String accompaniment, String lyric) {
        SongInformation vodSong = new SongInformation();
        vodSong.setCode(item.getSongId());
        vodSong.setSongInitial(StringUtils.isEmpty(item.getSongInitial()) ? "" : item.getSongInitial().toUpperCase());
        vodSong.setSongName(songName);
        vodSong.setSinger(getIdNames(item.getSingerMid(), item.getSinger()));
        vodSong.setLyricist(getIdNames(item.getLyricistMid(), item.getLyricist()));
        vodSong.setComposer(getIdNames(item.getComposerMid(), item.getComposer()));
        vodSong.setWordCount(item.getWordCount());


        vodSong.setSongType(createSongType(item));

        vodSong.setLanguage(new CodeName(item.getLanguage().getId(), item.getLanguage().getLanguageName()));
        List<CodeNameParent> tagCodeNames = new ArrayList<>();
        if (!StringUtils.isEmpty(item.getLyricFileMd5())) {
            String[] tags = item.getLyricFileMd5().split("\\|");
            for (String tag : tags) {
                SongTag songTag = songTagRepository.findByName(tag);
                if (songTag == null) {
                    songTag = songTagRepository.insert(tag, songName, item.getSinger());
                }
                CodeNameParent codeNameParent = new CodeNameParent();
                codeNameParent.setCode(songTag.getCode());
                codeNameParent.setName(songTag.getTagName());
                codeNameParent.setParentCode(0);
                codeNameParent.setParentName("");
                tagCodeNames.add(codeNameParent);
            }
        }
        vodSong.setTag(tagCodeNames);

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
        createSongVersion(vodSong.getCode(), vodSong.getSongName(), item, type, original, accompaniment, lyric);

        return vodSong;
    }

    private boolean isContainName(List<CodeNameParent> codeNames, String name) {
        for (CodeName codeName : codeNames) {
            if (codeName.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private SongSongVersion createSongVersion(Integer songId, String songName, Song song, int type, String original, String accompaniment, String lyric) {
        SongSongVersion version = new SongSongVersion();
        version.setCode(song.getSongId());//以旧歌曲版本命名版本id
        version.setSong(new CodeName(songId, songName));
        version.setSongCodeOld(song.getSongId());
        CodeName edition = getEdition(song.getVideoType());
        version.setType(type);
        version.setVersionsType(edition.getCode());
        version.setVersionsName(edition.getName());
        version.setSource(song.getSongSource() + "");

        version.setSinger(getIdNames(song.getSingerMid(), song.getSinger()));
        version.setLitigant(getIdNames(song.getLitigantMid(), song.getLitigant()));//诉讼权利人
        version.setProducer(getIdNames(song.getProducerMid(), song.getProducer()));//制作权利人
        version.setPublisher(getIdNames(song.getPublisherMid(), song.getPublisher()));//出品权利人
        version.setIncreaseHot(song.getHot());
        version.setVersionHot(0L);
        version.setVersionHotSum(song.getHot());
        version.setIssueTime(StringUtils.isEmpty(song.getLocalLyricFilePath()) ? null : DateUtil.string2Date(song.getLocalLyricFilePath()));//发行时间

        if (!StringUtils.isEmpty(song.getLyricFilePath())) {//专辑
            SongAlbum songAlbum = songAlbumRepository.findByName(song.getLyricFilePath());
            if (songAlbum == null) {
                songAlbum = songAlbumRepository.insert(song.getLyricFilePath(), song.getSongName(), song.getSinger());
            }
            List<CodeName> album = new ArrayList<>();
            album.add(new CodeName(songAlbum.getCode(), songAlbum.getAlbumName()));
            version.setAlbum(album);
        } else {
            version.setAlbum(null);
        }

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
        files.add(createFileDto(song, original, accompaniment, lyric));
        version.setVideoFileList(files);
//        List<String> ktvNetCode = null;
//        if (!StringUtils.isEmpty(song.getKtvNetCode())) {//定向歌曲信息
//            ktvNetCode = new ArrayList<>();
//            ktvNetCode.add(song.getKtvNetCode());
//        }
        SongSongVersion save = songSongVersionRepository.insert(version);
        versionCount++;
        return save;
    }


    public String fillMP3(Integer indexFrom, Integer indexTo) {
        System.out.println("清除MP3版本信息数量:" + songSongVersionRepository.cleanAllMP3Data());
        return fillMP3(indexFrom, indexTo, "mp3_msg.xlsx");
    }

    private String fillMP3(Integer indexFrom, Integer indexTo, String excelFile) {
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger countNotFound = new AtomicInteger(0);
        StringBuilder notFound = new StringBuilder();

        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = FillDataService.readExcel(excelFile);
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                String singer = CommonUtil.deleteSpecialChar(FillDataService.getString(sheet.getRow(row).getCell(0)));
                String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(FillDataService.getString(sheet.getRow(row).getCell(1))));
                String original = FillDataService.getString(sheet.getRow(row).getCell(2));
                String accompaniment = FillDataService.getString(sheet.getRow(row).getCell(3));
                String lyric = FillDataService.getString(sheet.getRow(row).getCell(4));
                System.out.println("第" + row + "行，歌名：" + songName + " 歌星：" + singer + " 原唱：" + original + " 伴唱：" + accompaniment
                        + " 歌词：" + lyric);
                if (!StringUtils.isEmpty(songName) && !StringUtils.isEmpty(singer)) {
                    String[] singers = singer.split("\\|");
                    List<String> singerList = Arrays.asList(singers);
                    SongSongVersion songSongVersion = songSongVersionRepository.findBySongNameAndSingerName(songName, singerList);
                    if (songSongVersion != null) {
//                        System.out.println("第" + row + "行，歌名：" + songName + " 歌星：" + singer + " 匹配到版本信息 code：" + songSongVersion.getCode() + " 歌名：" + songSongVersion.getSong().getName());
                        SongSongVersion songSongVersion1 = createMP3SongVersion(songSongVersion, original, accompaniment, lyric);
                        SongSongVersion insert = songSongVersionRepository.insert(songSongVersion1);
//                        System.out.println("增加音画版本：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(insert));
                        count.getAndIncrement();
                    } else {
                        SongInformation information = createSongInformationByMySQL(songName, singers, 2, original, accompaniment, lyric);
                        if (information != null) {
                            information.setStatus(1);
                            SongInformation save = songInformationRepository.insert(information);
                            System.out.println("增加音画版本歌曲信息：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                        } else {
                            information = createSongInformationByMp3Msg(songName, singers, original, accompaniment, lyric);
                            if (information != null) {
                                information.setStatus(0);
                                SongInformation save = songInformationRepository.insert(information);
                                System.out.println("通歌名、歌星增加音画版本歌曲信息：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                            } else {
                                countNotFound.getAndIncrement();
                                notFound.append("[").append(row).append(":").append(songName).append("]");
                            }
                        }
                    }
                }
            }
            String tip = "插入音画版版本总数：" + count.get() + " 无法匹配数量：" + countNotFound.get() + " 无法匹配：" + notFound.toString() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String toFirstChar(String chinese) {
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {//汉字
                atomicBoolean.set(true);
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else if ('\0' == newChar[i]) {
                atomicBoolean.set(true);
            } else {//非汉字
                if (atomicBoolean.get()) {
                    pinyinStr += newChar[i];
                    atomicBoolean.set(false);
                }
            }
        }
        return pinyinStr;
    }

    private SongInformation createSongInformationByMp3Msg(String songName, String[] singers, String original, String accompaniment, String lyric) {
        try {
            Song song = new Song();
            song.setId(0);
            song.setSongId(0);
            song.setSongName(songName);
            List<String> singerNames = Arrays.asList(singers);
            song.setSinger(CommonUtil.OBJECT_MAPPER.writeValueAsString(singerNames));
            List<Integer> singerId = new ArrayList<>();
            singerNames.forEach(item -> {
                Musician musician = mysqlMusicianRepository.findMusicianByMusicianName(item);
                singerId.add(musician == null ? 0 : musician.getMusicianId());
            });
            song.setSingerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(singerId));
            String initial = toFirstChar(songName);
            song.setSongInitial(initial);
            song.setWordCount(initial.length());
            SongType songType = new SongType();
            songType.setId(1);
            songType.setName("通俗");
            song.setSongTypeId(1);
            song.setSongType(songType);

            Language language = new Language();
            if (CommonUtil.isContainChinese(songName)) {
                language.setId(1);
                language.setLanguageName("国语");
            } else {
                language.setId(15);
                language.setLanguageName("其他语言");
            }
            song.setLanguageId(language.getId());
            song.setLanguage(language);

            song.setStatus(0);//
            return createSongInformation(song, songName, 2, original, accompaniment, lyric);
        } catch (Exception e) {
            log.error("创建新歌曲信息出错了：{}", e.getMessage());
        }
        return null;
    }

    private SongInformation createSongInformationByMySQL(String songName, String[] singers, int type, String o, String a, String l) {
        List<Song> songs = mysqlSongRepository.findAllBySongName(songName);
        if (songs != null && !songs.isEmpty()) {
            for (Song item : songs) {
                try {
                    List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(item.getSinger(), new TypeReference<List<String>>() {
                    });
                    if (matchElement(singers, singerNames) == singers.length) {
                        return createSongInformation(item, songName, type, o, a, l);
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static int matchElement(String[] names, List<String> nameList) {
        int matchCount = 0;
        if (names != null && names.length > 0 && !nameList.isEmpty()) {
            for (String name1 : names) {
                for (String name2 : nameList) {
                    if (name1.equalsIgnoreCase(name2)) {
                        matchCount++;
                    }
                }
            }
        }
        return matchCount;
    }

    private SongSongVersion createMP3SongVersion(SongSongVersion version, String original, String accompaniment, String lyric) {
        version.set_id(null);
        int versionCode = songSongVersionRepository.findMaxCode() + 1;
        version.setCode(versionCode);
        version.setSongCodeOld(0);
        version.setType(2);
        version.setNoteOne(null);
        version.setNoteTwo(null);
        version.setNoteThree(null);
        version.setCreateUser(0);
        version.setUpdateUser(0);
        version.setCreatedAt(new Date());
        version.setUpdatedAt(new Date());
        version.setSource(String.valueOf(0));
        version.setIncreaseHot(0L);
        version.setVersionHot(0L);
        version.setVersionHotSum(0L);
        version.setRecommend(0);
        version.setStatus(1);

        VideoFile videoFile;
        List<VideoFile> videoFiles = version.getVideoFileList();
        if (videoFiles != null && !videoFiles.isEmpty()) {
            videoFile = videoFiles.get(0);
            videoFile.setCode(versionCode);
            videoFile.setOriginalAudioFilePath(original);
            videoFile.setAccompanimentAudioFilePath(accompaniment);
            videoFile.setLyricFilePath(lyric);
            videoFile.setFileName(null);
            videoFile.setFilePath(null);
            videoFile.setResolutionWidth(1920);
            videoFile.setResolutionHeight(1080);
            videoFile.setScoreFilePath(null);
            videoFile.setCoordinatesFilePath(null);
            videoFile.setStatus(1);
        } else {
            videoFile = createFileDto(versionCode, original, accompaniment, lyric);
        }
        List<VideoFile> files = new ArrayList<>();
        files.add(videoFile);
        version.setVideoFileList(files);
        return version;
    }


    VideoFile createFileDto(int versionCode, String original, String accompaniment, String lyric) {
        VideoFile file = new VideoFile();
        file.setCode(versionCode);
        file.setFileName(null);
        file.setFilePath(null);
        file.setFormatName("H264");
        file.setVideoType("");
        file.setResolutionWidth(1920);
        file.setResolutionHeight(1080);
        file.setScoreFilePath(null);
        file.setCoordinatesFilePath(null);
        file.setAudioTrack(0);
        file.setVolume(80);
        file.setHot(0L);
        file.setRecommend(0);
        file.setStatus(1);
        file.setOriginalAudioFilePath(original);
        file.setAccompanimentAudioFilePath(accompaniment);
        file.setLyricFilePath(lyric);
        file.setRemark(null);
        return file;
    }

}
