package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.MaterialVideoDTO;
import com.bestarmedia.migration.model.mongo.VideoFile;
import com.bestarmedia.migration.model.mongo.ktv.*;
import com.bestarmedia.migration.model.mongo.song.*;
import com.bestarmedia.migration.repository.mongo.ktv.*;
import com.bestarmedia.migration.repository.mongo.song.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MigrateMongoSong2KtvService extends MigrateBase {

    private final SongAreaRepository songAreaRepository;
    private final SongInformationRepository songInformationRepository;
    private final SongLanguageRepository songLanguageRepository;
    private final SongMusicianRepository songMusicianRepository;
    private final SongSongTypeRepository songSongTypeRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongSongVersionTypeRepository songSongVersionTypeRepository;
    private final SongAlbumRepository songAlbumRepository;
    private final SongTagRepository songTagRepository;
    private final SongMaterialRepository songMaterialRepository;
    private final SongUgcRepository songUgcRepository;

    private final KtvSingerRepository ktvSingerRepository;
    private final KtvSongVersionRepository ktvSongVersionRepository;
    private final KtvSongRepository ktvSongRepository;
    private final KtvAreaRepository ktvAreaRepository;
    private final KtvLanguageRepository ktvLanguageRepository;
    private final KtvSongTypeRepository ktvSongTypeRepository;
    private final KtvSongVersionTypeRepository ktvSongVersionTypeRepository;
    private final KtvSongAlbumRepository ktvSongAlbumRepository;
    private final KtvTagRepository ktvTagRepository;
    private final KtvMaterialRepository ktvMaterialRepository;
    private final KtvUgcRepository ktvUgcRepository;

    private int musicianCount = 0;
    private Map<Integer, Boolean> types = new HashMap<>();
    private Map<String, Boolean> formats = new HashMap<>();

    @Autowired
    public MigrateMongoSong2KtvService(SongAreaRepository songAreaRepository,
                                       SongInformationRepository songInformationRepository,
                                       SongLanguageRepository songLanguageRepository,
                                       SongMusicianRepository songMusicianRepository,
                                       SongSongTypeRepository songSongTypeRepository,
                                       SongSongVersionRepository songSongVersionRepository,
                                       SongSongVersionTypeRepository songSongVersionTypeRepository,
                                       SongAlbumRepository songAlbumRepository,
                                       SongTagRepository songTagRepository,
                                       SongMaterialRepository songMaterialRepository,
                                       SongUgcRepository songUgcRepository,

                                       KtvSingerRepository ktvSingerRepository,
                                       KtvSongVersionRepository ktvSongVersionRepository,
                                       KtvSongRepository ktvSongRepository,
                                       KtvAreaRepository ktvAreaRepository,
                                       KtvLanguageRepository ktvLanguageRepository,
                                       KtvSongTypeRepository ktvSongTypeRepository,
                                       KtvSongVersionTypeRepository ktvSongVersionTypeRepository,
                                       KtvSongAlbumRepository ktvSongAlbumRepository,
                                       KtvTagRepository ktvTagRepository,
                                       KtvMaterialRepository ktvMaterialRepository,
                                       KtvUgcRepository ktvUgcRepository) {
        this.songAreaRepository = songAreaRepository;
        this.songInformationRepository = songInformationRepository;
        this.songLanguageRepository = songLanguageRepository;
        this.songMusicianRepository = songMusicianRepository;
        this.songSongTypeRepository = songSongTypeRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songSongVersionTypeRepository = songSongVersionTypeRepository;
        this.songAlbumRepository = songAlbumRepository;
        this.songTagRepository = songTagRepository;
        this.songMaterialRepository = songMaterialRepository;
        this.songUgcRepository = songUgcRepository;

        this.ktvSingerRepository = ktvSingerRepository;
        this.ktvSongVersionRepository = ktvSongVersionRepository;
        this.ktvSongRepository = ktvSongRepository;
        this.ktvAreaRepository = ktvAreaRepository;
        this.ktvLanguageRepository = ktvLanguageRepository;
        this.ktvSongTypeRepository = ktvSongTypeRepository;
        this.ktvSongVersionTypeRepository = ktvSongVersionTypeRepository;
        this.ktvSongAlbumRepository = ktvSongAlbumRepository;
        this.ktvTagRepository = ktvTagRepository;
        this.ktvMaterialRepository = ktvMaterialRepository;
        this.ktvUgcRepository = ktvUgcRepository;
    }

    public void setFormat(String typeFormat) {
        types.clear();
        formats.clear();
        String[] typeFormats = typeFormat.split("@");
        for (String tf : typeFormats) {
            String[] tfs = tf.split(",");
            types.put(Integer.parseInt(tfs[0]), true);
            formats.put(tfs[1], true);
        }
    }

    public String migrate(String typeFormat) {
        long currentTimeMillis = System.currentTimeMillis();
        mergeMaterial(typeFormat);
        mergeLanguage();
        mergeArea();
        mergeSongType();
        mergeSongVersionType();
        mergeAlbum();
        mergeTag();

        migrateMusician();

        mergeUgc();

        if (types.size() > 0 && formats.size() > 0) {
            migrateVersion2Ktv(typeFormat);
        }
        String tip = "Mongo.Song 迁移数据到 Mongo.Ktv 总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    public void mergeUgc() {
        List<SongUgc> songUgcs = songUgcRepository.findAll();
        System.out.println("清除 UGC 信息数量:" + songUgcRepository.cleanAllData());
        songUgcs.forEach(item -> {
            try {
                KtvUgc ugc = new KtvUgc();
                ugc.setCode(item.getCode());
                ugc.setSongName(item.getSongName());
                ugc.setSongInitial(item.getSongInitial());
                ugc.setWordCount(item.getWordCount());
                ugc.setSongType(item.getSongType());
                ugc.setSinger(item.getSinger());
                ugc.setLanguage(item.getLanguage());
                ugc.setStatus(item.getStatus());
                ugc.setUgcOwner(item.getUgcOwner());
                ugc.setFilePath(item.getFilePath());
                ugc.setResolutionWidth(item.getResolutionWidth());
                ugc.setResolutionHeight(item.getResolutionHeight());
                ugc.setAudioTrack(item.getAudioTrack());
                ugc.setVolume(item.getVolume());
                ugc.setRemark(item.getRemark());
                ugc.setCreatedAt(item.getCreatedAt());
                ugc.setUpdatedAt(item.getUpdatedAt());
                ugc.setDeletedAt(item.getDeletedAt());
                KtvUgc save = ktvUgcRepository.insert(ugc);
                System.out.println("保存 UGC 信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String mergeMaterial(String typeFormat) {
        setFormat(typeFormat);

        long time = System.currentTimeMillis();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<SongMaterial> materials = songMaterialRepository.findAll();
        System.out.println("清除素材信息数量:" + ktvMaterialRepository.cleanAllData());
        materials.forEach(item -> {
            try {
                List<MaterialVideoDTO> materialVideoDTOS = new ArrayList<>();

                if (item.getType() == 2 && item.getVideo() != null) {//视频类型
                    item.getVideo().forEach(videoDTO -> {
                        if (formats.containsKey(videoDTO.getFormatName())) {
                            materialVideoDTOS.add(videoDTO);
                        }
                    });
                }

                if ((item.getType() == 2 && !materialVideoDTOS.isEmpty()) || (item.getType() != 2 && !StringUtils.isEmpty(item.getImgFilePath()))) {
                    KtvMaterial material = new KtvMaterial();
                    material.setCode(item.getCode());
                    material.setMaterialName(item.getMaterialName());
                    material.setType(item.getType());
                    material.setVideo(item.getType() == 2 ? materialVideoDTOS.get(0) : null);
                    material.setImgFilePath(item.getType() != 2 ? item.getImgFilePath() : null);
                    material.setSongType(item.getSongType());
                    material.setMusicianCode(item.getMusicianCode());
                    material.setTag(item.getTag());
                    material.setSort(item.getSort());
                    material.setStatus(item.getStatus());
                    material.setRemark(item.getRemark());
                    material.setCreatedAt(item.getCreatedAt());
                    material.setUpdatedAt(item.getUpdatedAt());
                    material.setDeletedAt(item.getDeletedAt());
                    KtvMaterial save = ktvMaterialRepository.insert(material);
                    System.out.println("保存素材信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                    atomicInteger.getAndIncrement();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return "素材迁移总数:" + atomicInteger.get() + " 耗时:" + (System.currentTimeMillis() - time) / 1000;
    }

    private void mergeTag() {
        List<SongTag> songTags = songTagRepository.findAll();
        System.out.println("清除标签信息数量:" + ktvTagRepository.cleanAllData());
        songTags.forEach(item -> {
            try {
                KtvTag tag = new KtvTag();
                tag.setCode(item.getCode());
                tag.setName(item.getTagName());
                tag.setParentCode(item.getParentCode());
                tag.setRemark(item.getRemark());
                tag.setSort(item.getSort());
                tag.setStatus(item.getStatus());
                tag.setCreatedAt(item.getCreatedAt());
                tag.setUpdatedAt(item.getUpdatedAt());
                tag.setDeletedAt(item.getDeletedAt());
                KtvTag save = ktvTagRepository.insert(tag);
                System.out.println("保存标签信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeAlbum() {
        List<SongAlbum> songAlbums = songAlbumRepository.findAll();
        System.out.println("清除专辑信息数量:" + ktvSongAlbumRepository.cleanAllData());
        songAlbums.forEach(item -> {
            try {
                KtvAlbum album = new KtvAlbum();
                album.setCode(item.getCode());
                album.setAlbumName(item.getAlbumName());
                album.setImgFilePath(item.getImgFilePath());
                album.setRemark(item.getRemark());
                album.setCreatedAt(item.getCreatedAt());
                album.setUpdatedAt(item.getUpdatedAt());
                album.setDeletedAt(item.getDeletedAt());
                KtvAlbum save = ktvSongAlbumRepository.insert(album);
                System.out.println("保存专辑信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeArea() {
        List<SongArea> parts = songAreaRepository.findAll();
        long delCount = ktvAreaRepository.cleanAllData();
        System.out.println("清除地区信息数量量:" + delCount);
        parts.forEach(item -> {
            try {
                KtvArea area = new KtvArea();
                area.setCode(item.getCode());
                area.setName(item.getName());
                area.setParentCode(item.getParentCode());
                area.setIsShow(item.getIsShow());
                area.setSort(item.getSort());
                area.setCreatedAt(item.getCreatedAt());
                area.setUpdatedAt(item.getUpdatedAt());
                area.setDeletedAt(item.getDeletedAt());
                KtvArea save = ktvAreaRepository.insert(area);
                System.out.println("保存地区信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void mergeLanguage() {
        List<SongLanguage> languages = songLanguageRepository.findAll();
        long delCount = ktvLanguageRepository.cleanAllData();
        System.out.println("清除语种信息数量量:" + delCount);
        languages.forEach(item -> {
            try {
                KtvLanguage language = new KtvLanguage();
                language.setCode(item.getCode());
                language.setName(item.getName());
                language.setParentCode(item.getParentCode());
                language.setIsShow(item.getIsShow());
                language.setSort(item.getSort());
                language.setCreatedAt(item.getCreatedAt());
                language.setUpdatedAt(item.getUpdatedAt());
                language.setDeletedAt(item.getDeletedAt());
                KtvLanguage save = ktvLanguageRepository.insert(language);
                System.out.println("保存语种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void mergeSongVersionType() {
        List<SongVersionsType> versionsTypes = songSongVersionTypeRepository.findAll();
        long delCount = ktvSongVersionTypeRepository.cleanAllData();
        System.out.println("清除版本名称信息数量量:" + delCount);
        versionsTypes.forEach(item -> {
            try {
                KtvVersionName ktvSongType = new KtvVersionName();
                ktvSongType.setCode(item.getCode());
                ktvSongType.setName(item.getName());
                ktvSongType.setType(item.getType());
                ktvSongType.setSort(item.getSort());
                ktvSongType.setCreatedAt(item.getCreatedAt());
                ktvSongType.setUpdatedAt(item.getUpdatedAt());
                ktvSongType.setDeletedAt(item.getDeletedAt());
                KtvVersionName save = ktvSongVersionTypeRepository.insert(ktvSongType);
                System.out.println("保存版本名称曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void mergeSongType() {
        List<SongSongType> songTypes = songSongTypeRepository.findAll();
        long delCount = ktvSongTypeRepository.cleanAllData();
        System.out.println("清除曲种信息数量量:" + delCount);
        songTypes.forEach(item -> {
            try {
                KtvSongType songType = new KtvSongType();
                songType.setCode(item.getCode());
                songType.setName(item.getName());
                songType.setParentCode(item.getParentCode());
                songType.setIsShow(item.getIsShow());
                songType.setSort(item.getSort());
                songType.setCreatedAt(item.getCreatedAt());
                songType.setUpdatedAt(item.getUpdatedAt());
                songType.setDeletedAt(item.getDeletedAt());
                KtvSongType save = ktvSongTypeRepository.insert(songType);
                System.out.println("保存曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public String migrateMusician() {
        long delCount = ktvSingerRepository.cleanAllData();
        System.out.println("清除音乐人信息数量量:" + delCount);
        musicianCount = 0;
        long cur = System.currentTimeMillis();
        int count = (int) songMusicianRepository.countWarehousing();
        final int pageSize = 1000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = 0; i < size; i++) {
            migrateMusician(i, pageSize);
        }
        String text = "音乐人总量：" + count + " 迁移音乐人总数：" + musicianCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }


    private void migrateMusician(int page, int pageSize) {
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人 Mongo.Song >>> Mongo.Ktv 页码：" + page + " 页长：" + pageSize);
        List<SongMusician> list = songMusicianRepository.indexWarehousingMusician(page, pageSize);
        list.forEach(item -> {
            KtvSinger ktvSinger = new KtvSinger();
            ktvSinger.setCode(item.getCode());
            ktvSinger.setMusicianName(item.getMusicianName());
            ktvSinger.setMusicianType(item.getMusicianType());
            ktvSinger.setSimplicity(item.getSimplicity());
            ktvSinger.setWordCount(item.getWordCount());
            ktvSinger.setHot(item.getHot());
            ktvSinger.setHotSum(item.getHotSum());
            ktvSinger.setSex(item.getSex());
            ktvSinger.setBirthday(item.getBirthday());
            ktvSinger.setRole(item.getRole());
            ktvSinger.setPart(item.getPart());
            ktvSinger.setImgFilePath(item.getImgFilePath());
            ktvSinger.setStatus(item.getStatus());
            ktvSinger.setAlias(item.getAlias());
            ktvSinger.setSearchKeywords(item.getSearchKeywords());
            ktvSinger.setCreatedAt(item.getCreatedAt());
            ktvSinger.setUpdatedAt(item.getUpdatedAt());
            ktvSinger.setDeletedAt(item.getDeletedAt());
            ktvSingerRepository.insert(ktvSinger);
            musicianCount++;
        });
    }

    private void saveKtvSong(SongInformation item, List<KtvVersionSimple> versionFileSimples) {
        KtvSong ktvSong = new KtvSong();
        ktvSong.setCode(item.getCode());
        ktvSong.setSongName(item.getSongName());
        ktvSong.setSongInitial(item.getSongInitial());
        ktvSong.setWordCount(item.getWordCount());
        ktvSong.setSongType(item.getSongType());
        ktvSong.setSinger(item.getSinger());
        ktvSong.setLyricist(item.getLyricist());
        ktvSong.setComposer(item.getComposer());
        ktvSong.setLanguage(item.getLanguage());
        ktvSong.setTag(item.getTag());
        ktvSong.setHot(item.getHot());
        ktvSong.setHotSum(item.getHotSum());
        ktvSong.setRecommend(item.getRecommend());
        ktvSong.setStatus(item.getStatus());
        ktvSong.setVersions(versionFileSimples);
        ktvSong.setCreatedAt(item.getCreatedAt());
        ktvSong.setUpdatedAt(item.getUpdatedAt());
//            ktvSong.setSongVersionSimples(songSongVersionRepository.findKtvSongVersion(ktvSong.getCode()));
        ktvSongRepository.insert(ktvSong);
    }


    public String migrateVersion2Ktv(String typeFormat) {
        System.out.println("清除歌曲信息数量:" + ktvSongRepository.cleanAllData());
        System.out.println("清除版本信息数量:" + ktvSongVersionRepository.cleanAllData());
        return migrateVersion2Ktv(typeFormat, 0);

    }

    public String migrateVersion2Ktv(String typeFormat, int startPage) {
        AtomicInteger songCount = new AtomicInteger(0);
        setFormat(typeFormat);
        long cur = System.currentTimeMillis();
        int count = (int) songInformationRepository.count();
        final int pageSize = 1000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = startPage; i < size; i++) {
            int migrateCount = migrateSongInfo(i, pageSize);
            songCount.set(songCount.get() + migrateCount);
        }
        String text = "歌曲总量：" + count + " 迁移歌曲数量：" + songCount.get() + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }

    public int migrateSongInfo(int page, int pageSize) {
        AtomicInteger addSongCount = new AtomicInteger(0);

        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲信息 Mongo.Song >>> Mongo.Ktv 页码：" + page + " 页长：" + pageSize);
        List<SongInformation> list = songInformationRepository.indexSong(page, pageSize);
        list.forEach(item -> {
            AtomicInteger addVersionCount = new AtomicInteger(0);
            List<SongSongVersion> versions = songSongVersionRepository.findSongVersion(item.getCode());
            List<KtvVersionSimple> versionSimples = new ArrayList<>();
            if (versions != null && !versions.isEmpty()) {
                versions.forEach(v -> {

                    if (types.containsKey(v.getType()) && v.getVideoFileList() != null && !v.getVideoFileList().isEmpty()) {
                        List<VideoFile> files = new ArrayList<>();
                        List<KtvVersionFileSimple> fileSimples = new ArrayList<>();
                        v.getVideoFileList().forEach(it -> {
                            if (formats.containsKey(it.getFormatName())) {//符合格式要求
                                files.add(it);
                                fileSimples.add(new KtvVersionFileSimple(it.getCode(), it.getFilePath(), it.getVideoType(), it.getResolutionWidth(), it.getResolutionHeight(), it.getScoreFilePath(),
                                        it.getCoordinatesFilePath(), it.getOriginalAudioFilePath(), it.getAccompanimentAudioFilePath(), it.getLyricFilePath(), it.getStatus()));
                            }
                        });
                        if (!files.isEmpty()) {
                            KtvSongVersion version = new KtvSongVersion();
                            version.setCode(v.getCode());
                            version.setSong(v.getSong());
                            version.setSongCodeOld(v.getSongCodeOld());
                            version.setSinger(v.getSinger());
                            version.setType(v.getType());
                            version.setVersionsType(v.getVersionsType());
                            version.setSource(v.getSource());
                            version.setAlbum(v.getAlbum());
                            version.setIncreaseHot(v.getIncreaseHot());
                            version.setVersionHot(v.getVersionHot());
                            version.setVersionHotSum(v.getVersionHotSum());
                            version.setRecommend(v.getRecommend());
                            version.setStatus(v.getStatus());
                            version.setFile(files.get(0));
                            version.setCreatedAt(item.getCreatedAt());
                            version.setUpdatedAt(item.getUpdatedAt());
                            KtvSongVersion save = ktvSongVersionRepository.insert(version);

                            versionSimples.add(new KtvVersionSimple(save.getCode(), save.getType(), save.getVersionsType(), save.getVersionHotSum(),
                                    save.getRecommend(), save.getStatus(), fileSimples.get(0)));

                            addVersionCount.getAndIncrement();
                        }
                    }
                });
            }
            if (addVersionCount.get() > 0) {
                saveKtvSong(item, versionSimples);
                addSongCount.getAndIncrement();
            }
        });

        return addSongCount.get();
    }

}
