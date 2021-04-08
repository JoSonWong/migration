package com.bestarmedia.migration.controller;

import com.bestarmedia.migration.misc.JsonResponse;
import com.bestarmedia.migration.misc.JsonResponseHandler;
import com.bestarmedia.migration.service.*;
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
    private final MigrateMySQL2MongoKtvService migrateMySQL2MongoKtvService;

    @Autowired
    public MigrateController(Migrate2MongoSongAndHandlerDataService migrate2MongoSongAndHandlerDataService,
                             MigrateMongoSong2VodService migrateMongoSong2VodService,
                             MigrateMySQL2MongoSongService migrateMySQL2MongoSongService,
                             MigrateMongoSong2KtvService migrateMongoSong2KtvService,
                             MigrateMySQL2MongoKtvService migrateMySQL2MongoKtvService) {
        this.migrate2MongoSongAndHandlerDataService = migrate2MongoSongAndHandlerDataService;
        this.migrateMongoSong2VodService = migrateMongoSong2VodService;
        this.migrateMySQL2MongoSongService = migrateMySQL2MongoSongService;
        this.migrateMongoSong2KtvService = migrateMongoSong2KtvService;
        this.migrateMySQL2MongoKtvService = migrateMySQL2MongoKtvService;
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
        map.put("mysql_to_song", migrate2MongoSongAndHandlerDataService.migrate());
        map.put("tip_mp3", migrateMySQL2MongoSongService.fillMP3(0, 0));
        map.put("clean_musician", migrate2MongoSongAndHandlerDataService.cleanMusician());
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-data/mysql-mp3-to-mongo-song")
    public JsonResponse mysqlToMp3() {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMySQL2MongoSongService.migrateMySqlMp3ToSong());
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

    @GetMapping("/v7.0/migration/mongo-song-to-ktv-musician")
    public JsonResponse musicianToSong2Ktv() {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMongoSong2KtvService.migrateMusician());
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song-to-ugc")
    public JsonResponse ugcToSong() {
        HashMap<String, String> map = new HashMap<>();
        migrateMySQL2MongoSongService.migrateUgc();
        map.put("tip", "1");
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mongo-song-to-ktv-ugc")
    public JsonResponse ugcToKTV() {
        HashMap<String, String> map = new HashMap<>();
        migrateMongoSong2KtvService.mergeUgc();
        map.put("tip", "1");
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-file-path-to-mongo-song")
    public JsonResponse fillFilePath(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("original", migrateMySQL2MongoSongService.replaceOriginalFilePath(from, to, "mp3_original.xlsx"));
        map.put("original_1", migrateMySQL2MongoSongService.replaceOriginalFilePath(from, to, "mp3_1_original.xlsx"));
        map.put("accompany", migrateMySQL2MongoSongService.replaceAccompanimentFilePath(from, to, "mp3_accompany.xlsx"));
        map.put("accompany_1", migrateMySQL2MongoSongService.replaceAccompanimentFilePath(from, to, "mp3_1_accompany.xlsx"));
        map.put("lyric", migrateMySQL2MongoSongService.replaceLyricFilePath(from, to, "mp3_lyric.xlsx"));
        map.put("lyric_1", migrateMySQL2MongoSongService.replaceLyricFilePath(from, to, "mp3_1_lyric.xlsx"));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/fill-lyric-file-path-to-mongo-song")
    public JsonResponse fillLyricPath(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("lyric", migrateMySQL2MongoSongService.replaceLyricFilePath(from, to, "mp3_lyric.xlsx"));
//        map.put("lyric_1", migrateMySQL2MongoSongService.replaceLyricFilePath(from, to, "mp3_1_lyric.xlsx"));
        return JsonResponseHandler.success(map);
    }

    @GetMapping("/v7.0/migration/mysql-song-list-to-mongo-ktv")
    public JsonResponse migrateSongList() {
        HashMap<String, String> map = new HashMap<>();
        map.put("tip", migrateMySQL2MongoKtvService.migrate());
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-tag-to-song")
    public JsonResponse fillTag(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
//        map.put("tip", migrateMySQL2MongoSongService.fillTag(from, to, "tag_top_3000.xlsx"));
        map.put("tip", migrateMySQL2MongoSongService.fillTag(from, to, "tag_top_8000.xlsx"));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-tag-to-ktv")
    public JsonResponse fillTag2KTV(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
//        map.put("tip", migrateMongoSong2KtvService.fillTag(from, to, "tag_top_3000.xlsx"));
        map.put("tip", migrateMongoSong2KtvService.fillTag(from, to, "tag_top_8000.xlsx"));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-lyric-to-ktv")
    public JsonResponse fillLyric2KTV(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        map.put("lyric", migrateMongoSong2KtvService.replaceLyricFilePath(from, to, "mp3_lyric.xlsx"));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-singer-img-to-song")
    public JsonResponse fillSingerImg2Song(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        long cleanCount = migrateMongoSong2KtvService.cleanSingerImage();
        System.out.println("清除歌星图片素材数量：" + cleanCount);
        map.put("img", migrateMongoSong2KtvService.importSingerImage(from, to, "singer_0.xlsx"));
        map.put("img2", migrateMongoSong2KtvService.importSingerImage(from, to, "singer_1.xlsx"));
        return JsonResponseHandler.success(map);
    }


    @GetMapping("/v7.0/migration/fill-singer-img-to-ktv")
    public JsonResponse fillSingerImg2Ktv(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "to", defaultValue = "0") Integer to) {
        HashMap<String, String> map = new HashMap<>();
        long cleanCount = migrateMongoSong2KtvService.cleanKtvSingerImage();
        System.out.println("清除歌星图片素材数量：" + cleanCount);
        map.put("img", migrateMongoSong2KtvService.importSingerImage2KTV(from, to, "singer_0.xlsx"));
        map.put("img2", migrateMongoSong2KtvService.importSingerImage2KTV(from, to, "singer_1.xlsx"));
        return JsonResponseHandler.success(map);
    }
}
