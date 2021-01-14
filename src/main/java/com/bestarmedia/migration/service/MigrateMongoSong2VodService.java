package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.MaterialVideoDTO;
import com.bestarmedia.migration.model.mongo.VideoFile;
import com.bestarmedia.migration.model.mongo.song.*;
import com.bestarmedia.migration.model.mongo.vod.*;
import com.bestarmedia.migration.repository.mongo.song.*;
import com.bestarmedia.migration.repository.mongo.vod.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class MigrateMongoSong2VodService extends MigrateBase {

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

    private final VodSingerRepository vodSingerRepository;
    private final VodSongVersionRepository vodSongVersionRepository;
    private final VodSongRepository vodSongRepository;
    private final VodAreaRepository vodAreaRepository;
    private final VodLanguageRepository vodLanguageRepository;
    private final VodSongTypeRepository vodSongTypeRepository;
    private final VodSongVersionTypeRepository vodSongVersionTypeRepository;
    private final VodSongAlbumRepository vodSongAlbumRepository;
    private final VodTagRepository vodTagRepository;
    private final VodMaterialRepository vodMaterialRepository;

    private int versionCount = 0;
    private int songCount = 0;
    private int musicianCount = 0;
    private Map<Integer, Boolean> types = new HashMap<>();
    private Map<String, Boolean> formats = new HashMap<>();

    @Autowired
    public MigrateMongoSong2VodService(SongAreaRepository songAreaRepository,
                                       SongInformationRepository songInformationRepository,
                                       SongLanguageRepository songLanguageRepository,
                                       SongMusicianRepository songMusicianRepository,
                                       SongSongTypeRepository songSongTypeRepository,
                                       SongSongVersionRepository songSongVersionRepository,
                                       SongSongVersionTypeRepository songSongVersionTypeRepository,
                                       SongAlbumRepository songAlbumRepository,
                                       SongTagRepository songTagRepository,
                                       SongMaterialRepository songMaterialRepository,

                                       VodSingerRepository vodSingerRepository,
                                       VodSongVersionRepository vodSongVersionRepository,
                                       VodSongRepository vodSongRepository,
                                       VodAreaRepository vodAreaRepository,
                                       VodLanguageRepository vodLanguageRepository,
                                       VodSongTypeRepository vodSongTypeRepository,
                                       VodSongVersionTypeRepository vodSongVersionTypeRepository,
                                       VodSongAlbumRepository vodSongAlbumRepository,
                                       VodTagRepository vodTagRepository,
                                       VodMaterialRepository vodMaterialRepository) {
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

        this.vodSingerRepository = vodSingerRepository;
        this.vodSongVersionRepository = vodSongVersionRepository;
        this.vodSongRepository = vodSongRepository;
        this.vodAreaRepository = vodAreaRepository;
        this.vodLanguageRepository = vodLanguageRepository;
        this.vodSongTypeRepository = vodSongTypeRepository;
        this.vodSongVersionTypeRepository = vodSongVersionTypeRepository;
        this.vodSongAlbumRepository = vodSongAlbumRepository;
        this.vodTagRepository = vodTagRepository;
        this.vodMaterialRepository = vodMaterialRepository;

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

        if (types.size() > 0 && formats.size() > 0) {
            migrateVersion();
        }
        String tip = "Mongo.Song 迁移数据到 Mongo.Vod 总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    public String mergeMaterial(String typeFormat) {
        setFormat(typeFormat);

        long time = System.currentTimeMillis();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        List<SongMaterial> materials = songMaterialRepository.findAll();
        System.out.println("清除素材信息数量:" + vodMaterialRepository.cleanAllData());
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
                    VodMaterial material = new VodMaterial();
                    material.setCode(item.getCode());
                    material.setMaterialName(item.getMaterialName());
                    material.setType(item.getType());
                    material.setVideo(item.getType() == 2 ? materialVideoDTOS : null);
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
                    VodMaterial save = vodMaterialRepository.insert(material);
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
        System.out.println("清除标签信息数量:" + vodTagRepository.cleanAllData());
        songTags.forEach(item -> {
            try {
                VodTag tag = new VodTag();
                tag.setCode(item.getCode());
                tag.setTagName(item.getTagName());
                tag.setParentCode(item.getParentCode());
                tag.setRemark(item.getRemark());
                tag.setSort(item.getSort());
                tag.setStatus(item.getStatus());
                tag.setCreatedAt(item.getCreatedAt());
                tag.setUpdatedAt(item.getUpdatedAt());
                tag.setDeletedAt(item.getDeletedAt());
                VodTag save = vodTagRepository.insert(tag);
                System.out.println("保存标签信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeAlbum() {
        List<SongAlbum> songAlbums = songAlbumRepository.findAll();
        System.out.println("清除专辑信息数量:" + vodSongAlbumRepository.cleanAllData());
        songAlbums.forEach(item -> {
            try {
                VodAlbum album = new VodAlbum();
                album.setCode(item.getCode());
                album.setAlbumName(item.getAlbumName());
                album.setImgFilePath(item.getImgFilePath());
                album.setRemark(item.getRemark());
                album.setCreatedAt(item.getCreatedAt());
                album.setUpdatedAt(item.getUpdatedAt());
                album.setDeletedAt(item.getDeletedAt());
                VodAlbum save = vodSongAlbumRepository.insert(album);
                System.out.println("保存专辑信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeArea() {
        List<SongArea> parts = songAreaRepository.findAll();
        long delCount = vodAreaRepository.cleanAllData();
        System.out.println("清除地区信息数量量:" + delCount);
        parts.forEach(item -> {
            try {
                VodArea area = new VodArea();
                area.setCode(item.getCode());
                area.setName(item.getName());
                area.setParentCode(item.getParentCode());
                area.setIsShow(item.getIsShow());
                area.setSort(item.getSort());
                area.setCreatedAt(item.getCreatedAt());
                area.setUpdatedAt(item.getUpdatedAt());
                area.setDeletedAt(item.getDeletedAt());
                VodArea save = vodAreaRepository.insert(area);
                System.out.println("保存地区信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void mergeLanguage() {
        List<SongLanguage> languages = songLanguageRepository.findAll();
        long delCount = vodLanguageRepository.cleanAllData();
        System.out.println("清除语种信息数量量:" + delCount);
        languages.forEach(item -> {
            try {
                VodLanguage language = new VodLanguage();
                language.setCode(item.getCode());
                language.setName(item.getName());
                language.setParentCode(item.getParentCode());
                language.setIsShow(item.getIsShow());
                language.setSort(item.getSort());
                language.setCreatedAt(item.getCreatedAt());
                language.setUpdatedAt(item.getUpdatedAt());
                language.setDeletedAt(item.getDeletedAt());
                VodLanguage save = vodLanguageRepository.insert(language);
                System.out.println("保存语种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void mergeSongVersionType() {
        List<SongVersionsType> versionsTypes = songSongVersionTypeRepository.findAll();
        long delCount = vodSongVersionTypeRepository.cleanAllData();
        System.out.println("清除版本名称信息数量量:" + delCount);
        versionsTypes.forEach(item -> {
            try {
                VodVersionsType vodSongType = new VodVersionsType();
                vodSongType.setCode(item.getCode());
                vodSongType.setName(item.getName());
                vodSongType.setType(item.getType());
                vodSongType.setSort(item.getSort());
                vodSongType.setCreatedAt(item.getCreatedAt());
                vodSongType.setUpdatedAt(item.getUpdatedAt());
                vodSongType.setDeletedAt(item.getDeletedAt());
                VodVersionsType save = vodSongVersionTypeRepository.insert(vodSongType);
                System.out.println("保存版本名称曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void mergeSongType() {
        List<SongSongType> songTypes = songSongTypeRepository.findAll();
        long delCount = vodSongTypeRepository.cleanAllData();
        System.out.println("清除曲种信息数量量:" + delCount);
        songTypes.forEach(item -> {
            try {
                VodSongType songType = new VodSongType();
                songType.setCode(item.getCode());
                songType.setName(item.getName());
                songType.setParentCode(item.getParentCode());
                songType.setIsShow(item.getIsShow());
                songType.setSort(item.getSort());
                songType.setCreatedAt(item.getCreatedAt());
                songType.setUpdatedAt(item.getUpdatedAt());
                songType.setDeletedAt(item.getDeletedAt());
                VodSongType save = vodSongTypeRepository.insert(songType);
                System.out.println("保存曲种信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public String migrateMusician() {
        long delCount = vodSingerRepository.cleanAllData();
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
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人 Mongo.Song >>> Mongo.Vod 页码：" + page + " 页长：" + pageSize);
        List<SongMusician> list = songMusicianRepository.indexWarehousingMusician(page, pageSize);
        list.forEach(item -> {
            VodSinger vodSinger = new VodSinger();
            vodSinger.setCode(item.getCode());
            vodSinger.setMusicianName(item.getMusicianName());
            vodSinger.setMusicianType(item.getMusicianType());
            vodSinger.setSimplicity(item.getSimplicity());
            vodSinger.setWordCount(item.getWordCount());
            vodSinger.setHot(item.getHot());
            vodSinger.setHotSum(item.getHotSum());
            vodSinger.setSex(item.getSex());
            vodSinger.setBirthday(item.getBirthday());
            vodSinger.setRole(item.getRole());
            vodSinger.setPart(item.getPart());
            vodSinger.setImgFilePath(item.getImgFilePath());
            vodSinger.setStatus(item.getStatus());
            vodSinger.setAlias(item.getAlias());
            vodSinger.setSearchKeywords(item.getSearchKeywords());
            vodSinger.setCreatedAt(item.getCreatedAt());
            vodSinger.setUpdatedAt(item.getUpdatedAt());
            vodSinger.setDeletedAt(item.getDeletedAt());
            vodSingerRepository.insert(vodSinger);
            musicianCount++;
        });
    }

    public String migrateVersion() {
        System.out.println("清除歌曲信息数量:" + vodSongRepository.cleanAllData());
        System.out.println("清除版本信息数量:" + vodSongVersionRepository.cleanAllData());
        versionCount = 0;
        long cur = System.currentTimeMillis();
        int count = (int) songSongVersionRepository.count();
        final int pageSize = 1000;
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 版本总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
        for (int i = 0; i < size; i++) {
            migrateVersion(i, pageSize);
        }
        String text = "版本总量：" + count + " 迁移版本数量：" + versionCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
        return text;
    }


    public void migrateVersion(int page, int pageSize) {
        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 版本信息 Mongo.Song >>> Mongo.Vod 页码：" + page + " 页长：" + pageSize);
        List<SongSongVersion> list = songSongVersionRepository.indexVersion(page, pageSize);
        list.forEach(item -> {
            if (types.containsKey(item.getType()) && item.getVideoFileList() != null && !item.getVideoFileList().isEmpty()) {
                List<VideoFile> files = new ArrayList<>();
                item.getVideoFileList().forEach(it -> {
                    if (formats.containsKey(it.getFormatName())) {
                        files.add(it);
                    }
                });
                if (!files.isEmpty()) {
                    VodSongVersion version = new VodSongVersion();
                    version.setCode(item.getCode());
                    version.setSong(item.getSong());
                    version.setSongCodeOld(item.getSongCodeOld());
                    version.setSinger(item.getSinger());
                    version.setType(item.getType());
                    version.setVersionsTypeCode(item.getVersionsType());
                    version.setSource(item.getSource());
                    version.setAlbum(item.getAlbum());
                    version.setIncreaseHot(item.getIncreaseHot());
                    version.setVersionHot(item.getVersionHot());
                    version.setVersionHotSum(item.getVersionHotSum());
                    version.setRecommend(item.getRecommend());
                    version.setStatus(item.getStatus());
                    version.setVideoFileList(item.getVideoFileList());
                    version.setCreatedAt(item.getCreatedAt());
                    version.setUpdatedAt(item.getUpdatedAt());

                    VodSongVersion save = vodSongVersionRepository.insert(version);
                    versionCount++;
                    SongInformation songInformation;
                    if (vodSongRepository.findByCodeNotStatus(save.getSong().getCode()) == null
                            && (songInformation = songInformationRepository.findByCode(save.getSong().getCode())) != null) {
                        saveVodSong(songInformation);
                    }
                }
            }
        });
    }

    private void saveVodSong(SongInformation item) {
        VodSong vodSong = new VodSong();
        vodSong.setCode(item.getCode());
        vodSong.setSongName(item.getSongName());
        vodSong.setSongInitial(item.getSongInitial());
        vodSong.setWordCount(item.getWordCount());
        vodSong.setSongType(item.getSongType());
        vodSong.setSinger(item.getSinger());
        vodSong.setLyricist(item.getLyricist());
        vodSong.setComposer(item.getComposer());
        vodSong.setLanguage(item.getLanguage());
        vodSong.setTag(item.getTag());
        vodSong.setHot(item.getHot());
        vodSong.setHotSum(item.getHotSum());
        vodSong.setRecommend(item.getRecommend());
        vodSong.setStatus(item.getStatus());
        vodSong.setDiskFileStatus(1);
        vodSong.setCreatedAt(item.getCreatedAt());
        vodSong.setUpdatedAt(item.getUpdatedAt());
//            vodSong.setSongVersionSimples(songSongVersionRepository.findVodSongVersion(vodSong.getCode()));
        vodSongRepository.insert(vodSong);
        songCount++;
    }

//    private String migrateSong() {
//        System.out.println("清除歌曲信息数量:" + vodSongRepository.cleanAllData());
//        songCount = 0;
//        long cur = System.currentTimeMillis();
//        int count = (int) songInformationRepository.count();
//        final int pageSize = 1000;
//        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
//        for (int i = 0; i < size; i++) {
//            migrateSong(i, pageSize);
//        }
//        String text = "歌曲信息总量：" + count + " 迁移数：" + songCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
//        return text;
//    }
//
//    private void migrateSong(int page, int pageSize) {
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 歌曲信息 Mongo.Song >>> Mongo.Vod 页码：" + page + " 页长：" + pageSize);
//        List<SongInformation> list = songInformationRepository.indexSong(page, pageSize);
//        list.forEach(item -> saveVodSong(item));
//    }
}
