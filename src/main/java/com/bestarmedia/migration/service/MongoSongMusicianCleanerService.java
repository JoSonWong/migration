package com.bestarmedia.migration.service;


import com.bestarmedia.migration.model.mongo.TagSimple;
import com.bestarmedia.migration.model.mongo.song.SongInformation;
import com.bestarmedia.migration.model.mongo.song.SongMaterial;
import com.bestarmedia.migration.model.mongo.song.SongMusicianSimple;
import com.bestarmedia.migration.model.mongo.song.SongTag;
import com.bestarmedia.migration.repository.mongo.ktv.KtvMaterialRepository;
import com.bestarmedia.migration.repository.mongo.song.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MongoSongMusicianCleanerService {


    private final SongMusicianRepository songMusicianRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongInformationRepository songInformationRepository;
    private final SongTagRepository songTagRepository;
    private final SongMaterialRepository songMaterialRepository;
    private final KtvMaterialRepository ktvMaterialRepository;
    private long delCount = 0;


    @Autowired
    public MongoSongMusicianCleanerService(SongMusicianRepository songMusicianRepository,
                                           SongSongVersionRepository songSongVersionRepository,
                                           SongInformationRepository songInformationRepository,
                                           SongTagRepository songTagRepository,
                                           SongMaterialRepository songMaterialRepository,
                                           KtvMaterialRepository ktvMaterialRepository) {
        this.songMusicianRepository = songMusicianRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songInformationRepository = songInformationRepository;
        this.songTagRepository = songTagRepository;
        this.songMaterialRepository = songMaterialRepository;
        this.ktvMaterialRepository = ktvMaterialRepository;

    }


    public String cleanMusician() {
        long current = System.currentTimeMillis();
        delCount = 0;
        long count = songMusicianRepository.count();
        System.out.println("音乐人总量：" + count);
        final int pageSize = 1000;
        long size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        long updateImageFileCount = songMusicianRepository.updateImageFilePathEmpty();
        System.out.println("音乐人总量：" + count + " 默认图片置空数量：" + updateImageFileCount);

        for (int i = 0; i < size; i++) {
            List<SongMusicianSimple> list = songMusicianRepository.findMusician(i, pageSize);
            list.forEach(item -> {
                try {
                    System.out.println("检查音乐人 code:" + item.getCode() + " name:" + item.getMusicianName());
                    boolean isUsed = songInformationRepository.musicianCodeIsUsed(item.getCode());//歌曲信息是否有关联
                    System.out.println("检查音乐人 code:" + item.getCode() + " 歌曲信息是否有关联:" + isUsed);
                    if (!isUsed) {
                        isUsed = songSongVersionRepository.musicianCodeIsUsed(item.getCode());//版本信息是否有关联
                        System.out.println("检查音乐人 code:" + item.getCode() + " 版本信息是否有关联:" + isUsed);
                    }
                    if (!isUsed) {//音乐人无任何关联，删除
                        long del = songMusicianRepository.removeByCode(item.getCode());
                        System.out.println("无任何关联！删除音乐人 id:" + item.getCode() + " 删除结果:" + del);
                        delCount = delCount + delCount;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        String tip = "音乐人清理，总数量：" + count + " 清理数量：" + delCount + " 耗时：" + (System.currentTimeMillis() - current) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    public void handleTag() {
        createTags();
        fillMaterialTag();
        fillSongTag();
    }

    public void createTags() {
        songTagRepository.cleanAllData();
        String[] tagParent = new String[]{"爱情", "心情", "友情", "其他"};
        String[][] tags = new String[][]{
                new String[]{"爱情", "苦情", "甜蜜", "初恋", "失恋", "表白", "婚姻", "单恋"},
                new String[]{"心情", "寂寞", "快乐", "伤感", "平静", "思念", "宣泄", "离别", "治愈"},
                new String[]{"友情", "兄弟情", "姐妹情", "校园"},
                new String[]{"其他", "励志", "爱国", "祝福", "贺年", "亲情"}};
        for (int i = 0; i < tags.length; i++) {
            int code = i + 1;
            String name = tagParent[i];
            SongTag songTag = songTagRepository.insert(new SongTag(code, name, 0, "", 1, 100, ""));
            String[] sub = tags[i];
            for (int j = 0; j < sub.length; j++) {
                songTagRepository.insert(new SongTag((i + 1) * 100 + j, sub[j], songTag.getCode(), songTag.getTagName(), 1, 0, ""));
            }
        }
    }


    public void fillMaterialTag() {
        List<SongMaterial> songMaterials = songMaterialRepository.findAll();
        songMaterials.forEach(item -> {
            if (item.getTag() != null && !item.getTag().isEmpty()) {
                songMaterialRepository.updateTag(item.getCode(), findTag(item.getTag()));
                log.info("更新歌曲[{}]标签", item.getCode());
            }
        });
    }

    public void fillSongTag() {
        final int pageSize = 1000;
        int count = (int) songInformationRepository.count();
        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
        for (int i = 0; i < size; i++) {
            log.info("扫码歌曲标签：第 [{}] 页", i);
            List<SongInformation> list = songInformationRepository.indexSong(i, pageSize);
            list.forEach(item -> {
                if (item.getTag() != null && !item.getTag().isEmpty()) {
                    songInformationRepository.updateTag(item.getCode(), findTag(item.getTag()));
                    log.info("更新歌曲[{}]标签", item.getCode());
                }
            });
        }
    }

    private List<TagSimple> findTag(List<TagSimple> list) {
        List<TagSimple> tags = new ArrayList<>();
        list.forEach(tag -> {
            SongTag songTag = songTagRepository.findByName(tag.getTagName());
            if (songTag != null) {
                TagSimple tagSimple = new TagSimple();
                tagSimple.setTagCode(songTag.getCode());
                tagSimple.setTagName(songTag.getTagName());
                tagSimple.setParentCode(songTag.getParentCode());
                tagSimple.setParentName(songTag.getParentName());
                tags.add(tagSimple);
            }
        });
        return tags;
    }

}
