//package com.bestarmedia.migration.service;
//
//
//import com.bestarmedia.migration.misc.DateUtil;
//import com.bestarmedia.migration.model.mongo.MusicianFileFormat;
//import com.bestarmedia.migration.model.mongo.vod.VodSinger;
//import com.bestarmedia.migration.repository.mongo.vod.VodSingerRepository;
//import com.bestarmedia.migration.repository.mongo.vod.VodSongRepository;
//import com.bestarmedia.migration.repository.mongo.vod.VodSongVersionRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//@Service
//@Slf4j
//public class MongoVodMusicianScannerService {
//
//    private final VodSingerRepository vodSingerRepository;
//    private final VodSongRepository vodSongRepository;
//    private final VodSongVersionRepository vodSongVersionRepository;
//    private int musicianCount;
//    private List<String> allFileFormat;
//
//    @Autowired
//    public MongoVodMusicianScannerService(VodSingerRepository vodSingerRepository,
//                                          VodSongRepository vodSongRepository,
//                                          VodSongVersionRepository vodSongVersionRepository) {
//        this.vodSingerRepository = vodSingerRepository;
//        this.vodSongRepository = vodSongRepository;
//        this.vodSongVersionRepository = vodSongVersionRepository;
//    }
//
//
//    public String scanMusicianFileFormat() {
//        allFileFormat = vodSongVersionRepository.findAllFileFormat();
//        musicianCount = 0;
//        long cur = System.currentTimeMillis();
//        int count = (int) vodSingerRepository.countWarehousing();
//        final int pageSize = 100;
//        int size = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
//        size = 2;
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人总量：" + count + " 分页数：" + size + " 每页条数：" + pageSize);
//        for (int i = 0; i < size; i++) {
//            migrateMusician(i, pageSize);
//        }
//        String text = "音乐人总量：" + count + " 计算对应文件总数：" + musicianCount + " 耗时：" + ((System.currentTimeMillis() - cur) / 1000) + "秒";
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " " + text);
//        return text;
//    }
//
//    private void migrateMusician(int page, int pageSize) {
//        System.out.println(DateUtil.getDateTime(Calendar.getInstance().getTime()) + " 音乐人 计算音乐人对应文件格式数量 页码：" + page + " 页长：" + pageSize);
//        List<VodSinger> list = vodSingerRepository.indexWarehousingMusician(page, pageSize);
//        if (list != null) {
//            list.forEach(singer -> {
//                List<MusicianFileFormat> fileFormats = new ArrayList<>();
//                allFileFormat.forEach(fileFormat -> {
//                    int count = vodSongRepository.findSingerFileFormatCount(singer.getCode(), fileFormat);
//                    fileFormats.add(new MusicianFileFormat(fileFormat, count));
//                });
//                vodSingerRepository.update(singer.getCode(), fileFormats);
//            });
//        }
//    }
//
//
//    public int testSingerFile(Integer singerCode) {
//        return vodSongRepository.findSingerFileFormatCount(singerCode, "H264");
//    }
//}
