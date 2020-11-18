package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.MongoSongMusicianCleanerService;
import com.bestarmedia.migration.service.MongoSongMusicianScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MusicianScanController {

    private final MongoSongMusicianScannerService mongoSongMusicianScannerService;
    private final MongoSongMusicianCleanerService mongoSongMusicianCleanerService;


    @Autowired
    public MusicianScanController(MongoSongMusicianScannerService mongoSongMusicianScannerService, MongoSongMusicianCleanerService mongoSongMusicianCleanerService) {
        this.mongoSongMusicianScannerService = mongoSongMusicianScannerService;
        this.mongoSongMusicianCleanerService = mongoSongMusicianCleanerService;
    }

    @GetMapping("/v7.0/migration/song/musician-scan")
    public JsonResponse scanSongMusician() {
        HashMap<String, String> map = new HashMap<>();
        map.put("scan", mongoSongMusicianScannerService.scanSongMusician());
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/version/musician-scan")
    public JsonResponse scanVersionMusician() {
        HashMap<String, String> map = new HashMap<>();
        map.put("scan", mongoSongMusicianScannerService.scanVersionMusician());
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/musician/cleaner")
    public JsonResponse musicianClean() {
        HashMap<String, String> map = new HashMap<>();
        map.put("clean", mongoSongMusicianCleanerService.cleanMusician());
        return JsonResponseHandler.success(map);
    }
}
