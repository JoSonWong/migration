package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.Migrate2MongoSongAndHandlerDataService;
import com.bestarmedia.migration.service.MigrateMongoSong2VodService;
import com.bestarmedia.migration.service.MigrateMySQL2MongoSongService;
import com.bestarmedia.migration.service.MongoSongMusicianScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MigrateController {

    private final MigrateMySQL2MongoSongService migrateMySQL2MongoSongService;
    private final MongoSongMusicianScannerService mongoSongMusicianScannerService;
    private final Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService;
    private final MigrateMongoSong2VodService migrateMongoSong2VodService;

    @Autowired
    public MigrateController(Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService,
                             MigrateMongoSong2VodService migrateMongoSong2VodService,
                             MigrateMySQL2MongoSongService migrateMySQL2MongoSongService,
                             MongoSongMusicianScannerService mongoSongMusicianScannerService) {
        this.migrate2MongoSongAndHandlerDataService = migrate2MongoSongAndHandlerDataService;
        this.migrateMongoSong2VodService = migrateMongoSong2VodService;
        this.migrateMySQL2MongoSongService = migrateMySQL2MongoSongService;
        this.mongoSongMusicianScannerService = mongoSongMusicianScannerService;

    }

    @GetMapping("/v7.0/migration/mysql-to-mongo/{typeFormats}")
    public JsonResponse migrate(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = migrate2MongoSongAndHandlerDataService.migrate();
        String tip2Vod = migrateMongoSong2VodService.migrate(typeFormats);
        map.put("tip", tip2Song + "；" + tip2Vod);
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mysql-to-mongo-song")
    public JsonResponse toMongoSong() {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = migrate2MongoSongAndHandlerDataService.migrate();
        map.put("tip", tip2Song);
        map.put("tip_mp3", migrateMySQL2MongoSongService.fillMP3(0, 0));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/mongo-song-to-vod/{typeFormats}")
    public JsonResponse toMongoVod(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2VodService.migrate(typeFormats));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/mysql-to-mongo-song/musician")
    public JsonResponse toMysql2MongoMusician() {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = this.migrateMySQL2MongoSongService.migrateMusician();
        map.put("tip", tip2Song);
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mysql-to-mongo-song/song")
    public JsonResponse toMysql2MongoSong() {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = this.migrateMySQL2MongoSongService.migrateSong();
        map.put("tip", tip2Song);
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/mongo-song/song-musician/scan")
    public JsonResponse scanSongMusician() {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = this.mongoSongMusicianScannerService.scanSongMusician();
        map.put("tip", tip2Song);
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song/version-musician/scan")
    public JsonResponse scanVersionMusician() {
        HashMap<String, String> map = new HashMap<>();
        String tip2Song = this.mongoSongMusicianScannerService.scanVersionMusician();
        map.put("tip", tip2Song);
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song-to-vod/{typeFormats}/material")
    public JsonResponse songMaterial2Vod(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2VodService.mergeMaterial(typeFormats));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/mp3-to-mongo-song")
    public JsonResponse fillMP3ToMongoSong(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMySQL2MongoSongService.fillMP3(from, to));
        return JsonResponseHandler.success(map);
    }
}
