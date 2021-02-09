package com.bestarmedia.migration.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Migrate2MongoSongAndHandlerDataService {

    //    private final FillDataService fillDataService;
    private final MigrateMySQL2MongoSongService migrateMySQL2MongoSongService;
    private final MongoSongMusicianScannerService mongoSongMusicianScannerService;
    private final MongoSongMusicianCleanerService mongoSongMusicianCleanerService;

    @Autowired
    public Migrate2MongoSongAndHandlerDataService(
            MigrateMySQL2MongoSongService migrateMySQL2MongoSongService,
            MongoSongMusicianScannerService mongoSongMusicianScannerService,
            MongoSongMusicianCleanerService mongoSongMusicianCleanerService) {
//        this.fillDataService = fillDataService;
        this.migrateMySQL2MongoSongService = migrateMySQL2MongoSongService;
        this.mongoSongMusicianScannerService = mongoSongMusicianScannerService;
        this.mongoSongMusicianCleanerService = mongoSongMusicianCleanerService;
    }

    public String migrate() {
        long current = System.currentTimeMillis();
        migrateMySQL2MongoSongService.migrate();
        String tip = ("MySQL 迁移数据到 Mongo.Song 耗时：" + (System.currentTimeMillis() - current) / 1000);
        System.out.println(tip);

        return tip;
    }

    public String cleanMusician() {
        long current = System.currentTimeMillis();
        mongoSongMusicianScannerService.scanMusician();
        System.out.println("扫描 Mongo.Song 中歌曲、版本的音乐人耗时：" + (System.currentTimeMillis() - current) / 1000);
        current = System.currentTimeMillis();
        mongoSongMusicianCleanerService.cleanMusician();
        String tip = ("清理 Mongo.Song 中无关联音乐人耗时：" + (System.currentTimeMillis() - current) / 1000);
        System.out.println(tip);
        return tip;
    }


}
