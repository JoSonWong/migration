package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.SearchKeyword;
import com.bestarmedia.migration.model.mongo.song.*;
import com.bestarmedia.migration.repository.mongo.song.SongInformationRepository;
import com.bestarmedia.migration.repository.mongo.song.SongMusicianRepository;
import com.bestarmedia.migration.repository.mongo.song.SongSongTypeRepository;
import com.bestarmedia.migration.repository.mongo.song.SongSongVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class MongoSongMusicianScannerService {

    private final SongMusicianRepository songMusicianRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongInformationRepository songInformationRepository;
    private final SongSongTypeRepository songTypeRepository;
    private int createdMusicianCount = 0;
    private int updatedMusicianCount = 0;


    @Autowired
    public MongoSongMusicianScannerService(SongMusicianRepository songMusicianRepository,
                                           SongSongVersionRepository songSongVersionRepository,
                                           SongInformationRepository songInformationRepository,
                                           SongSongTypeRepository songTypeRepository) {
        this.songMusicianRepository = songMusicianRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songInformationRepository = songInformationRepository;
        this.songTypeRepository = songTypeRepository;
    }

    public void scanMusician() {
        scanSongMusician();
        scanVersionMusician();
    }


    public String scanVersionMusician() {
        long current = System.currentTimeMillis();
        createdMusicianCount = 0;
        updatedMusicianCount = 0;
        long count = songSongVersionRepository.count();
        System.out.println("版本信息总量：" + count);
        final int pageSize = 1000;
        long size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        for (int i = 0; i < size; i++) {
            List<SongSongVersionSimple> list = songSongVersionRepository.findSong(i, pageSize);
            System.out.println("获取版本信息，分页：" + i + " 条数：" + pageSize + " 数据：" + list.size());

            list.forEach(item -> {
                try {
                    boolean needUpdate = false;
                    List<CodeName> singer = item.getSinger();
                    if (singer != null && !singer.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(1, singer);
                        if (codeNames != null) {
                            item.setSinger(codeNames);
                            needUpdate = true;
                        }
                    }
                    List<CodeName> litigant = item.getLitigant();
                    if (litigant != null && !litigant.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(4, litigant);
                        if (codeNames != null) {
                            item.setLitigant(codeNames);
                            needUpdate = true;
                        }
                    }
                    List<CodeName> producer = item.getProducer();
                    if (producer != null && !producer.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(5, producer);
                        if (codeNames != null) {
                            item.setProducer(codeNames);
                            needUpdate = true;
                        }
                    }
                    List<CodeName> publisher = item.getPublisher();
                    if (publisher != null && !publisher.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(6, publisher);
                        if (codeNames != null) {
                            item.setPublisher(codeNames);
                            needUpdate = true;
                        }
                    }
                    if (needUpdate) {
                        long updateCount = songSongVersionRepository.update(item);
                        System.out.println("更新版本信息：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item) + " 更新数量：" + updateCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        String tip = "从版本信息整理，创建音乐人数量：" + createdMusicianCount + " 更新音乐人数量：" + updatedMusicianCount + " 耗时：" + (System.currentTimeMillis() - current) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    public String scanSongMusician() {
        long current = System.currentTimeMillis();
        createdMusicianCount = 0;
        updatedMusicianCount = 0;
        long count = songInformationRepository.count();
        final int pageSize = 1000;
        int size = (int) (count % pageSize == 0 ? count / pageSize : (count / pageSize + 1));
        System.out.println("歌曲信息总量：" + count + " 分页数：" + size);
        for (int i = size - 1; i >= 0; i--) {
            List<SongInformationSimple> list = songInformationRepository.findSong(i, pageSize);
            System.out.println("获取歌曲信息，分页：" + i + " 条数：" + pageSize + " 数据：" + list.size());

            list.forEach(item -> {
                try {
                    boolean needUpdate = false;
                    List<CodeName> singer = item.getSinger();
                    if (singer != null && !singer.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(1, singer);
                        if (codeNames != null) {
                            item.setSinger(codeNames);
                            needUpdate = true;
                        }
                    }
                    List<CodeName> lyricist = item.getLyricist();
                    if (lyricist != null && !lyricist.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(2, lyricist);
                        if (codeNames != null) {
                            item.setLyricist(codeNames);
                            needUpdate = true;
                        }
                    }
                    List<CodeName> composer = item.getComposer();
                    if (composer != null && !composer.isEmpty()) {
                        List<CodeName> codeNames = createMusicians(3, composer);
                        if (codeNames != null) {
                            item.setComposer(codeNames);
                            needUpdate = true;
                        }
                    }
                    if (needUpdate) {
                        if (item.getSongType() != null) {
                            SongType songType = new SongType(item.getSongType().getCode(), item.getSongType().getName(), item.getSongType().getCode(), item.getSongType().getName());
                            SongSongType songSongType = songTypeRepository.findByCode(songType.getCode());
                            if (songSongType != null && songSongType.getParentCode() > 0) {
                                SongSongType parent = songTypeRepository.findByCode(songType.getParentCode());
                                if (parent != null) {
                                    songType.setParentCode(parent.getCode());
                                    songType.setParentName(parent.getName());
                                }
                            }
                            item.setSongType(songType);
                        }
                        long updateCount = songInformationRepository.update(item);
                        System.out.println("更新歌曲信息：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(item) + " 更新数量：" + updateCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        String tip = "从歌曲信息整理，创建音乐人数量：" + createdMusicianCount + " 更新音乐人数量：" + updatedMusicianCount + " 耗时：" + (System.currentTimeMillis() - current) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    private List<CodeName> createMusicians(int type, List<CodeName> codeNames) {
        List<CodeName> names = new ArrayList<>();
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        codeNames.forEach(item -> {
            try {
                if (!StringUtils.isEmpty(item.getName())) {
                    String musicianName = item.getName().replace("/", "|").replace("\\", "|")//张学友/刘德华 或 张学友\刘德华
                            .replace("丨", "|").replace("｜", "|").replace("、", "|").replace(";", "|").replace("；", "|")
                            .replace("(", "|").replace(")", "|").replace("（", "|").replace("）", "|").replace(",", "|").replace("，", "|")//AKB48(毛唯嘉,沈莹,叶知恩)
                            .replace("V.S", "|").replace("&", "|").replace("\"", "|").replace("“", "|").replace("”", "|");
                    if (CommonUtil.isAllChinese(musicianName)) {//中文名带空格说明是分隔符
                        musicianName = musicianName.replace(" ", "|");
                    }
                    if (CommonUtil.isContainChinese(musicianName)) {
                        musicianName = musicianName.replace("V.S", "|").replace("v.s", "|").replace("VS", "|").replace("vs", "|");
                    }
                    String[] musicianNames = musicianName.split("\\|");
                    for (String n : musicianNames) {
                        String name = CommonUtil.deleteSpaceAndUpperFirst(n);
                        SongMusician musician = songMusicianRepository.findSingerByName(name);
                        if (musician == null) {
                            SongMusician insertSave = songMusicianRepository.insert(createSongMusician(type, name));
                            System.out.println("创建音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(insertSave));
                            updateSearchKeywords(insertSave);
                            createdMusicianCount++;
                            names.add(new CodeName(insertSave.getCode(), insertSave.getMusicianName()));
                            if (item.getCode().equals(insertSave.getCode())) {
                                atomicBoolean.set(true);
                            }
                        } else {
                            List<Integer> types = musician.getMusicianType();
                            boolean exitsType = false;
                            for (Integer t : types) {
                                if (t == type) {
                                    exitsType = true;
                                }
                            }
                            if (!exitsType) {
                                types.add(type);
                                types.sort(Comparator.naturalOrder());
                                musician.setMusicianType(types);
                                long updateCount = songMusicianRepository.updateMusicianTypes(musician.getCode(), types);
                                System.out.println("更新音乐人类型 code：" + musician.getCode() + " 更新条数：" + updateCount + " 类型：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(types));
                                updatedMusicianCount++;
                            }
                            names.add(new CodeName(musician.getCode(), musician.getMusicianName()));
                            if (item.getCode().equals(musician.getCode())) {
                                atomicBoolean.set(true);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return atomicBoolean.get() ? names : null;
    }

    private long updateSearchKeywords(SongMusician singer) {
        List<SearchKeyword> searchKeywords = new ArrayList<>();
        SearchKeyword searchKeyword = new SearchKeyword();
        searchKeyword.setType(0);
        searchKeyword.setCode(singer.getCode());
        List<String> keywords = new ArrayList<>();
        keywords.add(singer.getMusicianName());
        if (!StringUtils.isEmpty(singer.getSimplicity())) {
            keywords.add(singer.getSimplicity());
        }
        List<String> alias = singer.getAlias();
        if (alias != null && !alias.isEmpty()) {
            keywords.addAll(alias);
        }
        searchKeyword.setKeywords(keywords);
        searchKeywords.add(searchKeyword);
        singer.setSearchKeywords(searchKeywords);
        return songMusicianRepository.updateSearchKeywords(singer.getCode(), searchKeywords);
    }

    private SongMusician createSongMusician(int type, String name) {
        SongMusician singer = new SongMusician();
        int maxCode = songMusicianRepository.getMaxCode();
        if (maxCode < 200000) {
            maxCode = 200000;
        } else {
            maxCode++;
        }
        singer.setCode(maxCode);
        singer.setMusicianName(CommonUtil.deleteSpaceAndUpperFirst(name));
//        singer.setSimplicity("");
//        singer.setWordCount(0);

        List<Integer> musicianTypes = new ArrayList<>();
        musicianTypes.add(type);
        singer.setMusicianType(musicianTypes);

//        singer.setRole(0);
//        singer.setSex(0);
//        singer.setHot(0L);
//        singer.setHotSum(0L);
//        singer.setBirthday(null);
//        singer.setPart(null);

//        singer.setImgFilePath("");
        singer.setStatus(1);
//        List<String> alias = new ArrayList<>();
//        singer.setAlias(alias);//别名

        singer.setMold(0);
//        singer.setRemark("");
        Date date = Calendar.getInstance().getTime();
        singer.setCreatedAt(date);
        singer.setUpdatedAt(date);
        singer.setDeletedAt(null);
        singer.setCreateUser(0);
        singer.setUpdateUser(0);
        return singer;
    }

}
