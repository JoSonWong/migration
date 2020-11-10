package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.MigrateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class MigrateController {

    private final MigrateService migrateService;

    @Autowired
    public MigrateController(MigrateService migrateService) {
        this.migrateService = migrateService;
    }

//    @GetMapping("/v7.0/vod-api/merge")
//    public JsonResponse getMerge() {
//        migratingData2MongoDb.merge();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/merge-song")
//    public JsonResponse getMergeSong() {
//        migratingData2MongoDb.mergeSong();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/merge-song/{index}")
//    public JsonResponse getMergeSongIndex(@PathVariable Integer index) {
//        migratingData2MongoDb.mergeSongIndex();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//
//    @GetMapping("/v7.0/vod-api/merge-musician")
//    public JsonResponse getMergeMusician() {
//        migratingData2MongoDb.mergeMusician();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/merge-task")
//    public JsonResponse getMergeTask() {
//        HashMap<String, MongoTaskData> map = new HashMap<>();
////        map.put("task", migratingData2MongoDb.getTaskData());
//        return JsonResponseHandler.success(map);
//    }
//
//
//    @GetMapping("/v7.0/vod-api/merge-language")
//    public JsonResponse getMergeLanguage() {
//        migratingData2MongoDb.mergeLanguage();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/merge-song-type")
//    public JsonResponse getMergeSongType() {
//        migratingData2MongoDb.mergeSongType();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/merge-part")
//    public JsonResponse getMergePart() {
//        migratingData2MongoDb.mergePart();
//        HashMap<String, String> map = new HashMap<>();
//        map.put("merge", "OK");
//        return JsonResponseHandler.success(map);
//    }
//
//    @GetMapping("/v7.0/vod-api/singer-replace-song/{singer}")
//    public JsonResponse getReplaceSongs(@PathVariable String singer) {
//        return JsonResponseHandler.success(migratingData2MongoDb.findSingerReplaceSongs(singer));
//    }

    @GetMapping("/v7.0/migration/singer-count")
    public JsonResponse getSingerCount() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("singer-count", migrateService.getSingerCountByName());
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/singer/{singerName}")
    public JsonResponse getSingerName(@PathVariable String singerName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("singer-name", migrateService.getSingerName(singerName));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/singer")
    public JsonResponse migrateSinger(@RequestParam(value = "to_database") Integer toDatabase) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateService.migrateMusician(toDatabase));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/song")
    public JsonResponse migrateSong(@RequestParam(value = "to_database") Integer toDatabase) {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateService.migrateSong(toDatabase));
        return JsonResponseHandler.success(map);
    }

}
