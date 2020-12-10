package com.bestarmedia.migration.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Migrate2MongoSongAndHandlerDataService {

    private final FillDataService fillDataService;
    private final MigrateMySQL2MongoSongService migrateMySQL2MongoSongService;
    private final MongoSongMusicianScannerService mongoSongMusicianScannerService;
    private final MongoSongMusicianCleanerService mongoSongMusicianCleanerService;

    @Autowired
    public Migrate2MongoSongAndHandlerDataService(
            FillDataService fillDataService,
            MigrateMySQL2MongoSongService migrateMySQL2MongoSongService,
            MongoSongMusicianScannerService mongoSongMusicianScannerService,
            MongoSongMusicianCleanerService mongoSongMusicianCleanerService) {
        this.fillDataService = fillDataService;
        this.migrateMySQL2MongoSongService = migrateMySQL2MongoSongService;
        this.mongoSongMusicianScannerService = mongoSongMusicianScannerService;
        this.mongoSongMusicianCleanerService = mongoSongMusicianCleanerService;
    }

    public String migrate() {
        long total = System.currentTimeMillis();
        long current = System.currentTimeMillis();
//        String fillExcelData = fillDataService.fill(0);
//        System.out.println("Excel 填充数据到 MySQL 耗时：" + (System.currentTimeMillis() - current) / 1000 + " 秒，返回结果：" + fillExcelData);
//        current = System.currentTimeMillis();
        migrateMySQL2MongoSongService.migrate();
        System.out.println("MySQL 迁移数据到 Mongo.Song 耗时：" + (System.currentTimeMillis() - current) / 1000);
        current = System.currentTimeMillis();
        mongoSongMusicianScannerService.scanMusician();
        System.out.println("扫描 Mongo.Song 中歌曲、版本的音乐人耗时：" + (System.currentTimeMillis() - current) / 1000);

        current = System.currentTimeMillis();
        mongoSongMusicianCleanerService.cleanMusician();
        System.out.println("清理 Mongo.Song 中无关联音乐人耗时：" + (System.currentTimeMillis() - current) / 1000);

        current = System.currentTimeMillis();
        fillDataService.fillReleaseTime();
        System.out.println("从 EXCEL 中更新发行时间耗时：" + (System.currentTimeMillis() - current) / 1000);

        String tip = "迁移数据并到 Mongo.Song 耗时:" + (System.currentTimeMillis() - total) / 1000;
        System.out.println(tip);

        return tip;
    }

}