package com.bestarmedia.migration.service;


import com.bestarmedia.migration.model.mongo.song.SongMusicianSimple;
import com.bestarmedia.migration.repository.mongo.song.SongInformationRepository;
import com.bestarmedia.migration.repository.mongo.song.SongMusicianRepository;
import com.bestarmedia.migration.repository.mongo.song.SongSongVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MongoSongMusicianCleanerService {


    private final SongMusicianRepository songMusicianRepository;
    private final SongSongVersionRepository songSongVersionRepository;
    private final SongInformationRepository songInformationRepository;
    private long delCount = 0;


    @Autowired
    public MongoSongMusicianCleanerService(SongMusicianRepository songMusicianRepository,
                                           SongSongVersionRepository songSongVersionRepository,
                                           SongInformationRepository songInformationRepository) {
        this.songMusicianRepository = songMusicianRepository;
        this.songSongVersionRepository = songSongVersionRepository;
        this.songInformationRepository = songInformationRepository;
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
}
