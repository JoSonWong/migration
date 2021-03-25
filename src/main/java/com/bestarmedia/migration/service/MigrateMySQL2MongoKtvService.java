package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.ktv.KtvSongList;
import com.bestarmedia.migration.model.mongo.ktv.KtvSongListSong;
import com.bestarmedia.migration.model.mysql.SongList;
import com.bestarmedia.migration.model.mysql.SongListSong;
import com.bestarmedia.migration.repository.mongo.ktv.KtvSongListRepository;
import com.bestarmedia.migration.repository.mongo.ktv.KtvSongListSongRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongListRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongListSongRepository;
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

    private final KtvSongListRepository ktvSongListRepository;
    private final KtvSongListSongRepository ktvSongListSongRepository;


    @Autowired
    public MigrateMySQL2MongoKtvService(MysqlSongListRepository mysqlSongListRepository,
                                        MysqlSongListSongRepository mysqlSongListSongRepository,
                                        KtvSongListRepository ktvSongListRepository,
                                        KtvSongListSongRepository ktvSongListSongRepository) {
        this.mysqlSongListRepository = mysqlSongListRepository;
        this.mysqlSongListSongRepository = mysqlSongListSongRepository;
        this.ktvSongListRepository = ktvSongListRepository;
        this.ktvSongListSongRepository = ktvSongListSongRepository;
    }


    public String migrate() {
        long currentTimeMillis = System.currentTimeMillis();
        mergeSongList();
        mergeSongListSong();
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
}
