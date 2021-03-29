package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.ktv.*;
import com.bestarmedia.migration.model.mysql.*;
import com.bestarmedia.migration.repository.mongo.ktv.*;
import com.bestarmedia.migration.repository.mysql.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class MigrateMySQL2MongoKtvService extends MigrateBase {

    private final MysqlSongListRepository mysqlSongListRepository;
    private final MysqlSongListSongRepository mysqlSongListSongRepository;
    private final MysqlEmojiRepository mysqlEmojiRepository;
    private final MysqlEmojiDetailRepository mysqlEmojiDetailRepository;
    private final MysqlBreakthroughRepository mysqlBreakthroughRepository;
    private final MysqlBreakthroughSongRepository mysqlBreakthroughSongRepository;
    private final MysqlBarrageRepository mysqlBarrageRepository;

    private final KtvSongListRepository ktvSongListRepository;
    private final KtvSongListSongRepository ktvSongListSongRepository;
    private final KtvEmojiPackageRepository ktvEmojiPackageRepository;
    private final KtvEmojiRepository ktvEmojiRepository;
    private final KtvBreakthroughRepository ktvBreakthroughRepository;
    private final KtvBreakthroughSongRepository ktvBreakthroughSongRepository;
    private final KtvBarrageRepository ktvBarrageRepository;

    @Autowired
    public MigrateMySQL2MongoKtvService(MysqlSongListRepository mysqlSongListRepository,
                                        MysqlSongListSongRepository mysqlSongListSongRepository,
                                        MysqlEmojiRepository mysqlEmojiRepository,
                                        MysqlEmojiDetailRepository mysqlEmojiDetailRepository,
                                        MysqlBreakthroughRepository mysqlBreakthroughRepository,
                                        MysqlBreakthroughSongRepository mysqlBreakthroughSongRepository,
                                        MysqlBarrageRepository mysqlBarrageRepository,

                                        KtvSongListRepository ktvSongListRepository,
                                        KtvSongListSongRepository ktvSongListSongRepository,
                                        KtvEmojiPackageRepository ktvEmojiPackageRepository,
                                        KtvEmojiRepository ktvEmojiRepository,
                                        KtvBreakthroughRepository ktvBreakthroughRepository,
                                        KtvBreakthroughSongRepository ktvBreakthroughSongRepository,
                                        KtvBarrageRepository ktvBarrageRepository
    ) {
        this.mysqlSongListRepository = mysqlSongListRepository;
        this.mysqlSongListSongRepository = mysqlSongListSongRepository;
        this.mysqlEmojiRepository = mysqlEmojiRepository;
        this.mysqlEmojiDetailRepository = mysqlEmojiDetailRepository;
        this.mysqlBreakthroughRepository = mysqlBreakthroughRepository;
        this.mysqlBreakthroughSongRepository = mysqlBreakthroughSongRepository;
        this.mysqlBarrageRepository = mysqlBarrageRepository;

        this.ktvSongListRepository = ktvSongListRepository;
        this.ktvSongListSongRepository = ktvSongListSongRepository;
        this.ktvEmojiPackageRepository = ktvEmojiPackageRepository;
        this.ktvEmojiRepository = ktvEmojiRepository;
        this.ktvBreakthroughRepository = ktvBreakthroughRepository;
        this.ktvBreakthroughSongRepository = ktvBreakthroughSongRepository;
        this.ktvBarrageRepository = ktvBarrageRepository;
    }


    public String migrate() {
        long currentTimeMillis = System.currentTimeMillis();
        mergeSongList();
        mergeSongListSong();
        mergeEmoji();
        mergeEmojiDetail();
        mergeBarrage();
        mergeBreakthrough();
        mergeBreakthroughSong();
        String tip = "MySQL 数据迁移 Mongo.KTV 总耗时：" + (System.currentTimeMillis() - currentTimeMillis) / 1000 + "秒";
        System.out.println(tip);
        return tip;
    }

    private void mergeSongList() {
        List<SongList> parts = mysqlSongListRepository.findAllByDeletedAtIsNull();
        long delCount = ktvSongListRepository.cleanAllData();
        System.out.println("清除歌单信息数量量:" + delCount);
        parts.forEach(item -> {
            try {
                KtvSongList songList = new KtvSongList();
                songList.setCode(item.getId());
                songList.setName(item.getName());
                songList.setImgFilePath(item.getImgPath());
                songList.setStatus(item.getStatus());
                songList.setSort(0);
                songList.setHot(item.getHot());
                songList.setUpdatedAt(new Date());
                songList.setCreatedAt(new Date());
                KtvSongList save = ktvSongListRepository.insert(songList);
                System.out.println("保存歌单信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void mergeSongListSong() {
        List<SongListSong> songListSongs = mysqlSongListSongRepository.findAllByDeletedAtIsNull();
        long delCount = ktvSongListSongRepository.cleanAllData();
        System.out.println("清除歌单中歌曲信息数量量:" + delCount);
        songListSongs.forEach(item -> {
            try {
                KtvSongListSong song = new KtvSongListSong();
                song.setCode(item.getId());
                song.setVersionCode(item.getSongId());
                song.setSongListCode(item.getSongListId());
                song.setSort(item.getSort());
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvSongListSong save = ktvSongListSongRepository.insert(song);
                System.out.println("保存歌单歌曲信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeBreakthroughSong() {
        List<BreakthroughSong> list = mysqlBreakthroughSongRepository.findAllByDeletedAtIsNull();
        ktvBreakthroughSongRepository.cleanAllData();
        list.forEach(item -> {
            try {
                KtvBreakthroughSong song = new KtvBreakthroughSong();
                song.setCode(item.getId());
                song.setVersionCode(item.getSongId());
                song.setBreakthroughCode(item.getBreakthroughId());
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvBreakthroughSong save = ktvBreakthroughSongRepository.insert(song);
                System.out.println("保存闯关歌曲信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeBreakthrough() {
        List<Breakthrough> list = mysqlBreakthroughRepository.findAllByDeletedAtIsNull();
        ktvBreakthroughRepository.cleanAllData();
        list.forEach(item -> {
            try {
                KtvBreakthrough song = new KtvBreakthrough();
                song.setCode(item.getId());
                song.setName(item.getName());
                song.setStatus(item.getStatus());
                song.setNum(0);
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvBreakthrough save = ktvBreakthroughRepository.insert(song);
                System.out.println("保存闯关信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void mergeBarrage() {
        List<Barrage> emojis = mysqlBarrageRepository.findAllByDeletedAtIsNull();
        ktvBarrageRepository.cleanAllData();
        emojis.forEach(item -> {
            try {
                KtvBarrage song = new KtvBarrage();
                song.setCode(item.getId());
                song.setContent(item.getContent());
                song.setStatus(item.getStatus());
                song.setSort(0);
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvBarrage save = ktvBarrageRepository.insert(song);
                System.out.println("保存弹幕信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeEmoji() {
        List<Emoji> emojis = mysqlEmojiRepository.findAllByDeletedAtIsNull();
        long delCount = ktvEmojiPackageRepository.cleanAllData();
        System.out.println("清除表情包:" + delCount);
        emojis.forEach(item -> {
            try {
                KtvEmojiPackage song = new KtvEmojiPackage();
                song.setCode(item.getId());
                song.setName(item.getName());
                song.setCover(item.getCoverImg());
                song.setStatus(item.getStatus());
                song.setSort(item.getSort());
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvEmojiPackage save = ktvEmojiPackageRepository.insert(song);
                System.out.println("保存表情包信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void mergeEmojiDetail() {
        List<EmojiDetail> songListSongs = mysqlEmojiDetailRepository.findAllByDeletedAtIsNull();
        long delCount = ktvEmojiRepository.cleanAllData();
        System.out.println("清除表情:" + delCount);
        songListSongs.forEach(item -> {
            try {
                KtvEmoji song = new KtvEmoji();
                song.setCode(item.getId());
                song.setName(item.getName());
                song.setEmojiPackageCode(item.getEmojiId());
                song.setThumbnailImage(item.getThumbnail());
                song.setStyleImage(item.getFilePath());
                song.setSort(0);
                song.setUpdatedAt(new Date());
                song.setCreatedAt(new Date());
                KtvEmoji save = ktvEmojiRepository.insert(song);
                System.out.println("保存表情信息:" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
