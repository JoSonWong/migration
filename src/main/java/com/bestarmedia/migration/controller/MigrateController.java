package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.Migrate2MongoSongAndHandlerDataService;
import com.bestarmedia.migration.service.MigrateMongoSong2VodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MigrateController {

    private final Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService;
    private final MigrateMongoSong2VodService migrateMongoSong2VodService;

    @Autowired
    public MigrateController(Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService,
                             MigrateMongoSong2VodService migrateMongoSong2VodService) {
        this.migrate2MongoSongAndHandlerDataService = migrate2MongoSongAndHandlerDataService;
        this.migrateMongoSong2VodService = migrateMongoSong2VodService;
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
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/mongo-song-to-vod/{typeFormats}")
    public JsonResponse toMongoVod(@PathVariable String typeFormats) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2VodService.migrate(typeFormats));
        return JsonResponseHandler.success(map);
    }
}
