package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.Migrate2MongoSongAndHandlerDataService;
import com.bestarmedia.migration.service.MigrateMongoSong2KtvService;
import com.bestarmedia.migration.service.MigrateMongoSong2VodService;
import com.bestarmedia.migration.service.MigrateMySQL2MongoSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MigrateController {

    private final MigrateMySQL2MongoSongService migrateMySQL2MongoSongService;
    private final Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService;
    private final MigrateMongoSong2VodService migrateMongoSong2VodService;
    private final MigrateMongoSong2KtvService migrateMongoSong2KtvService;

    @Autowired
    public MigrateController(Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService,
                             MigrateMongoSong2VodService migrateMongoSong2VodService,
                             MigrateMySQL2MongoSongService migrateMySQL2MongoSongService,
                             MigrateMongoSong2KtvService migrateMongoSong2KtvService) {
        this.migrate2MongoSongAndHandlerDataService = migrate2MongoSongAndHandlerDataService;
        this.migrateMongoSong2VodService = migrateMongoSong2VodService;
        this.migrateMySQL2MongoSongService = migrateMySQL2MongoSongService;
        this.migrateMongoSong2KtvService = migrateMongoSong2KtvService;
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


    @GetMapping("/v7.0/migration/mongo-song-to-ktv/{typeFormats}")
    public JsonResponse toSong2Ktv(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2KtvService.migrate(typeFormats));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song-to-ktv/{typeFormats}/material")
    public JsonResponse materialToSong2Ktv(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2KtvService.mergeMaterial(typeFormats));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song-to-ktv-song/{typeFormats}")
    public JsonResponse toSong2Ktv(@PathVariable String typeFormats, @RequestParam(value = "from") Integer from) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2KtvService.migrateVersion2Ktv(typeFormats, from));
        return JsonResponseHandler.success(map);
    }
}
