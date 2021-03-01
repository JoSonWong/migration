package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.misc.DateUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mysql.MusicianSimple;
import com.bestarmedia.migration.model.mysql.SongSimple;
import com.bestarmedia.migration.repository.mysql.MysqlMusicianSimpleRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongSimpleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FillDataService {

//    private final SongSongVersionRepository songSongVersionRepository;
//    private final VodSingerRepository vodSingerRepository;
//    private final VodSongVersionRepository vodSongVersionRepository;

    private final MysqlSongSimpleRepository mysqlSongSimpleRepository;
    private final MysqlMusicianSimpleRepository mysqlMusicianSimpleRepository;

    @Autowired
    public FillDataService(
//            SongSongVersionRepository songSongVersionRepository,
            MysqlSongSimpleRepository mysqlSongSimpleRepository,
            MysqlMusicianSimpleRepository mysqlMusicianSimpleRepository) {
//        this.songSongVersionRepository = songSongVersionRepository;
        this.mysqlSongSimpleRepository = mysqlSongSimpleRepository;
        this.mysqlMusicianSimpleRepository = mysqlMusicianSimpleRepository;
    }

    private boolean fill2Mysql(SongSimple song, List<CodeName> singers, List<CodeName> lyricists, List<CodeName> composers,
                               List<CodeName> litigants, List<CodeName> producers, List<CodeName> publishers, Date releaseDate, String album, String version) {
        try {
            if (song != null) {
                boolean update = false;
                boolean isContainSinger = false;//是否包含歌星
                boolean isMatchSinger = false;//是否完全匹配歌星
                if (singers != null && !singers.isEmpty()) {
                    List<String> singerNameList = new ArrayList<>();
                    try {
                        List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(song.getSinger(), new TypeReference<List<String>>() {
                        });
                        for (String e : singerNames) {
                            if (e.contains("|")) {
                                for (String singer : e.split("\\|")) {
                                    String s = singer.trim();
                                    if (!StringUtils.isEmpty(s)) {
                                        singerNameList.add(s);
                                    }
                                }
                            } else if (e.contains("｜")) {
                                for (String singer : e.split("｜")) {
                                    String s = singer.trim();
                                    if (!StringUtils.isEmpty(s)) {
                                        singerNameList.add(s);
                                    }
                                }
                            } else {
                                singerNameList.add(e);
                            }
                        }
                    } catch (Exception e) {//转换出错，说明数据库数据格式存储错误，纠正过来

                    }
                    int matchCount = matchElement(singers, singerNameList);
                    isContainSinger = matchCount > 0;
                    isMatchSinger = matchCount == singers.size() && singerNameList.size() == singers.size();

                }
                if (isContainSinger) {//其中一个歌星匹配，歌星词曲信息
                    if (lyricists != null && !lyricists.isEmpty()) {
                        List<Integer> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        lyricists.forEach(item -> {
                            ids.add(item.getCode());
                            names.add(item.getName());
                        });
                        song.setLyricistMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                        song.setLyricist(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                        update = true;
                    }
                    if (composers != null && !composers.isEmpty()) {
                        List<Integer> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        composers.forEach(item -> {
                            ids.add(item.getCode());
                            names.add(item.getName());
                        });
                        song.setComposerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                        song.setComposer(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                        update = true;
                    }

                    if (publishers != null && !publishers.isEmpty()) {
                        List<Integer> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        publishers.forEach(item -> {
                            ids.add(item.getCode());
                            names.add(item.getName());
                        });
                        song.setPublisherMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                        song.setPublisher(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                        update = true;
                    }

                    if (isMatchSinger) {//全部歌星匹配，才更新权利人信息
                        if (litigants != null && !litigants.isEmpty()) {
                            List<Integer> ids = new ArrayList<>();
                            List<String> names = new ArrayList<>();
                            litigants.forEach(item -> {
                                ids.add(item.getCode());
                                names.add(item.getName());
                            });
                            song.setLitigantMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                            song.setLitigant(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                            update = true;
                        }
                        if (producers != null && !producers.isEmpty()) {
                            List<Integer> ids = new ArrayList<>();
                            List<String> names = new ArrayList<>();
                            producers.forEach(item -> {
                                ids.add(item.getCode());
                                names.add(item.getName());
                            });
                            song.setProducerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                            song.setProducer(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                            update = true;
                        }
                        if (!StringUtils.isEmpty(version)) {
                            version = version.toUpperCase();
                            boolean isSameVersion = ("MTV".equalsIgnoreCase(version) && (song.getVideoType() == 1 || song.getVideoType() == 11))
                                    || ("演唱会".equalsIgnoreCase(version) && (song.getVideoType() == 2));
                            if (isSameVersion) {
                                if (releaseDate != null) {
                                    song.setLocalLyricFilePath(DateUtil.getDate(releaseDate));
                                    update = true;
                                }
                                if (!StringUtils.isEmpty(album)) {
                                    song.setLyricFilePath(album);
                                    update = true;
                                }
                            }
                        }
                    }
                }

                if (update) {
                    SongSimple save = mysqlSongSimpleRepository.save(song);
//                    System.out.println("保存歌曲到MySQL：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    private boolean isContainSinger(List<CodeName> codeNames, String dbSingerName) {
//        if (StringUtils.isEmpty(dbSingerName)) {//数据库没有数据，填充
//            return true;
//        }
//        List<String> singerNames;
//        try {
//            singerNames = CommonUtil.OBJECT_MAPPER.readValue(dbSingerName, new TypeReference<List<String>>() {
//            });
//        } catch (Exception e) {//转换出错，说明数据库数据格式存储错误，纠正过来
//            return true;
//        }
//        if (singerNames != null) {
//            return isContainElement(codeNames, singerNames);
//        } else {
//            return true;
//        }
//    }

//    private int isContainElement(List<CodeName> elements1, List<String> elements2) {
//        return matchElement(elements1, elements2);
//    }


    public static int matchElement(List<CodeName> elements1, List<String> elements2) {
        int matchCount = 0;
        if (!elements1.isEmpty() && !elements2.isEmpty()) {
            for (CodeName element1 : elements1) {
                for (String element2 : elements2) {
                    if (element1.getName().equalsIgnoreCase(element2)) {
                        matchCount++;
                    }
                }
            }
        }
        return matchCount;
    }

    public String fillBNS(Integer indexFrom, Integer indexTo) {
        fillBNS(indexFrom, indexTo, "song_message.xlsx");
        return fillBNS(indexFrom, indexTo, "bns2.xlsx");
    }

    public String fillBNS202102(Integer indexFrom, Integer indexTo) {
        return fillBNS(indexFrom, indexTo, "bns202102.xlsx");
    }

    private String fillBNS(Integer indexFrom, Integer indexTo, String excelFile) {
        AtomicInteger count = new AtomicInteger(0);
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel(excelFile);
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                String id = getString(sheet.getRow(row).getCell(0));
                String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(getString(sheet.getRow(row).getCell(1))));
                String singer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(2)));
                String lyricist = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(3)));
                String composer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(4)));
                String litigant = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(5)));
                String producer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(6)));
                String publisher = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(7)));
                String releaseTime = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(8)));
                String tag = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(9)));

//                System.out.println("第" + row + "行，id：" + id + " 歌名：" + songName + " 歌星：" + singer + " 词：" + lyricist + " 曲：" + composer
//                        + " 诉讼：" + litigant + " 制作：" + producer + " 出品：" + publisher + " 发行日期：" + releaseTime + " 标签：" + tag);
                Date releaseDate = StringUtils.isEmpty(releaseTime) ? null : DateUtil.string2Date(releaseTime);

                List<CodeName> singers = handlerMusicianMySQL(singer);
                List<CodeName> lyricists = handlerMusicianMySQL(lyricist);
                List<CodeName> composers = handlerMusicianMySQL(composer);
                List<CodeName> litigants = handlerMusicianMySQL(litigant);
                List<CodeName> producers = handlerMusicianMySQL(producer);
                List<CodeName> publishers = handlerMusicianMySQL(publisher);
                int updateCount = fill2Mysql(id, songName, singers, lyricists, composers, litigants, producers, publishers, releaseDate, null, "MTV", tag);
                count.set(count.get() + updateCount);
            }
            String tip = "更新歌曲总数：" + count.get() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private int fill2Mysql(String id, String songName, List<CodeName> singers, List<CodeName> lyricists, List<CodeName> composers,
                           List<CodeName> litigants, List<CodeName> producers, List<CodeName> publishers, Date releaseData, String album, String version, String tag) {
        AtomicInteger integer = new AtomicInteger(0);
        if (!StringUtils.isEmpty(id)) {
            try {
                SongSimple song = mysqlSongSimpleRepository.findSongBySongId(Integer.valueOf(id));
                if (song != null) {
                    if (!StringUtils.isEmpty(tag)) {
                        song.setLyricFileMd5(tag);
                    }
                    if (fill2Mysql(song, singers, lyricists, composers, litigants, producers, publishers, releaseData, album, version))
                        integer.getAndIncrement();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            List<SongSimple> songs = mysqlSongSimpleRepository.findAllBySongName(songName);
            if (songs != null) {
                songs.forEach(item -> {
                    if (fill2Mysql(item, singers, lyricists, composers, litigants, producers, publishers, releaseData, album, version))
                        integer.getAndIncrement();
                });
            }
        }
        return integer.get();
    }

    public String fillReleaseTimeAndTag(Integer indexFrom, Integer indexTo) {
        String ret = fillReleaseTimeAndTag(indexFrom, indexTo, "song_message.xlsx");
        return ret + " bns2：" + fillReleaseTimeAndTag(indexFrom, indexTo, "bns2.xlsx");
    }


    public String fillReleaseTimeAndTag(Integer indexFrom, Integer indexTo, String fileName) {
        AtomicLong updateCount = new AtomicLong(0);
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel(fileName);
            int size = indexTo > 0 ? indexTo : sheet.getPhysicalNumberOfRows();
            int from = indexFrom > 0 ? indexFrom : 1;
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                try {
                    String id = getString(sheet.getRow(row).getCell(0));
                    if (!StringUtils.isEmpty(id)) {
                        int songId = Integer.valueOf(id);
                        String releaseTime = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(8)));
                        String tag = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(9)));
                        System.out.println("第" + row + "行，id：" + id + " 发行日期：" + releaseTime + " 标签：" + tag);
                        Date releaseDate = StringUtils.isEmpty(releaseTime) ? null : DateUtil.string2Date(releaseTime);
                        int retCount = mysqlSongSimpleRepository.updateReleaseTimeAndTag(releaseDate == null ? "" : DateUtil.getDate(releaseDate), tag, songId);
                        updateCount.set(updateCount.get() + retCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String tip = "更新发行时间及歌曲标签总数量：" + updateCount.get() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private List<CodeName> handlerMusicianMySQL(String musicians) {
        List<CodeName> songSingers = new ArrayList<>();
        if (!StringUtils.isEmpty(musicians)) {
            String[] singers = musicians.split("\\|");
            for (String name : singers) {
                String n = CommonUtil.deleteSpaceAndUpperFirst(name);
//                System.out.println("查询歌星：" + n + " 信息 >>>>>>>");
                MusicianSimple musician = mysqlMusicianSimpleRepository.findFirstByMusicianName(n);
                if (musician == null) {//新建歌手信息
                    songSingers.add(new CodeName(0, n));
                } else {
                    songSingers.add(new CodeName(musician.getMusicianId(), n));
                }
            }
        }
        return songSingers;
    }

    /**
     * 把单元格的内容转为字符串
     *
     * @param xssfCell 单元格
     * @return 字符串
     */
    public static String getString(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }
        if (xssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
                return new SimpleDateFormat("yyyy-MM-dd").format(xssfCell.getDateCellValue());
            } else {
                NumberFormat nf = NumberFormat.getInstance();
                return String.valueOf(nf.format(xssfCell.getNumericCellValue())).replace(",", "");
            }
        } else if (xssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else {
            return xssfCell.getStringCellValue();
        }
    }


    public String fillYJX(Integer indexFrom, Integer indexTo) {
        AtomicInteger count = new AtomicInteger(0);
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel("MTV_yjx.xlsx");
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                String id = getString(sheet.getRow(row).getCell(0));
                String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(getString(sheet.getRow(row).getCell(1))));
                String singer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(2)));
                String version = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(3)));
                String language = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(4)));
                String producer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(5)));//制作
                String litigant = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(6)));
                //                String publisher = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(7)));

                System.out.println("第" + row + "行，id：" + id + " 歌名：" + songName + " 歌星：" + singer + " 版本：" + version + " 语种：" + language
                        + " 诉讼：" + litigant + " 制作：" + producer);

                List<SongSimple> songs = mysqlSongSimpleRepository.findAllBySongName(songName);

                if (songs != null) {
                    songs.forEach(item -> {
                        List<CodeName> singers = handlerMusicianMySQL(singer);
                        List<CodeName> litigants = handlerMusicianMySQL(litigant);
                        List<CodeName> producers = handlerMusicianMySQL(producer);
                        if (fill2Mysql(item, singers, null, null, litigants, producers, null, null, null, version))
                            count.getAndIncrement();
                    });
                }
            }
            String tip = "更新歌曲总数：" + count.get() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;

            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


//    private boolean isContainSinger(String excelSingerName, String dbSingerName) {
//        List<CodeName> codeNames = new ArrayList<>();
//        if (!StringUtils.isEmpty(excelSingerName)) {
//            String[] excelSingers = excelSingerName.split("\\|");
//            for (String singer : excelSingers) {
//                codeNames.add(new CodeName(0, singer));
//            }
//        }
//        return isContainSinger(codeNames, dbSingerName);
//    }

    public static XSSFSheet readExcel(String excelFile) {
        try {
            XSSFWorkbook wb;
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/" + excelFile);
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            return wb.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String fillLY(Integer indexFrom, int indexTo) {
        AtomicInteger count = new AtomicInteger(0);
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel("ly.xlsx");
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                String id = getString(sheet.getRow(row).getCell(0));
                String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(getString(sheet.getRow(row).getCell(1))));
                String singer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(2)));
                String lyricist = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(3)));
                String composer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(4)));
                String producer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(5)));
                String litigant = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(6)));
                System.out.println("第" + row + "行，id：" + id + " 歌名：" + songName + " 歌星：" + singer + " 词：" + lyricist + " 曲：" + composer
                        + " 诉讼：" + litigant + " 制作：" + producer);
                List<SongSimple> songs = mysqlSongSimpleRepository.findAllBySongName(songName);
                if (songs != null) {
                    songs.forEach(item -> {
                        List<CodeName> singers = handlerMusicianMySQL(singer);
                        List<CodeName> lyricists = handlerMusicianMySQL(lyricist);
                        List<CodeName> composers = handlerMusicianMySQL(composer);
                        List<CodeName> litigants = handlerMusicianMySQL(litigant);
                        List<CodeName> producers = handlerMusicianMySQL(producer);
                        if (fill2Mysql(item, singers, lyricists, composers, litigants, producers, null, null, null, ""))
                            count.getAndIncrement();
                    });
                }
            }
            String tip = "更新歌曲总数：" + count.get() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String fillTotal(Integer indexFrom, int indexTo) {
//        fillTotal(indexFrom, indexTo, "song_msg_total1.xlsx");
        fillTotal(indexFrom, indexTo, "song_msg_total2.xlsx");
        fillTotal(indexFrom, indexTo, "song_msg_total3.xlsx");
        fillTotal(indexFrom, indexTo, "song_msg_total4.xlsx");
        return fillTotal(indexFrom, indexTo, "song_msg_total5.xlsx");
    }

    public String fillTotal(Integer indexFrom, int indexTo, String fileName) {
        AtomicLong count = new AtomicLong(0);
        AtomicLong errorCount = new AtomicLong(0);
        StringBuilder errorRow = new StringBuilder();
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel(fileName);
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                try {
                    String id = getString(sheet.getRow(row).getCell(0));
                    String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(getString(sheet.getRow(row).getCell(9))));
                    String singer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(2)));
                    String album = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(3)));
                    String lyricist = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(4)));
                    String composer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(5)));
                    String producer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(6)));
//                String litigant = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(6)));
                    String releaseTime = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(7)));

                    String version = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(13)));
//                    System.out.println("第" + row + "行，id：" + id + " 发行日期：" + releaseTime);
                    Date releaseDate = StringUtils.isEmpty(releaseTime) ? null : DateUtil.string2Date(releaseTime);


                    List<SongSimple> songs = mysqlSongSimpleRepository.findAllBySongName(songName);

                    if (songs != null) {
                        songs.forEach(item -> {
                            List<CodeName> singers = handlerMusicianMySQL(singer);
                            List<CodeName> lyricists = handlerMusicianMySQL(lyricist);
                            List<CodeName> composers = handlerMusicianMySQL(composer);
                            List<CodeName> producers = handlerMusicianMySQL(producer);
                            if (fill2Mysql(item, singers, lyricists, composers, null, producers, null, releaseDate, album, version))
                                count.getAndIncrement();
                        });
                    }
                    if (count.get() % 100 == 0) {//更新100行输出一次
                        System.out.println("第" + row + "行，id：" + id + " 歌名：" + songName + " 歌星：" + singer + " 词：" + lyricist + " 曲：" + composer
                                + " 出品：" + producer + " 专辑：" + album + " 发行时间:" + (releaseDate == null ? "" : DateUtil.getDate(releaseDate)) + " 版本：" + version);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount.getAndIncrement();
                    errorRow.append(row).append(",");
                }
            }
            String tip = "更新歌曲总数：" + count.get() + " 出错数：" + errorCount.get() + " 出错行：" + errorRow.toString() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String fillYjxAll(Integer indexFrom, int indexTo) {
//        fillYjxAll(indexFrom, indexTo, "yinjixie_all1.xlsx");
        fillYjxAll(indexFrom, indexTo, "yinjixie_all2.xlsx");
        return fillYjxAll(indexFrom, indexTo, "yinjixie_all3.xlsx");
//        return fillYjxAll(indexFrom, indexTo, "yinjixie_all.xlsx");
    }

    public String fillYjxAll(Integer indexFrom, int indexTo, String fileName) {
        AtomicLong count = new AtomicLong(0);
        AtomicLong errorCount = new AtomicLong(0);
        StringBuilder errorRow = new StringBuilder();
        long time = System.currentTimeMillis();
        XSSFSheet sheet;
        try {
            sheet = readExcel(fileName);
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;
            if (indexFrom > 0) {
                from = indexFrom;
            }
            if (indexTo > 0) {
                size = indexTo;
            }
            //循环取每行的数据
            for (int row = from; row < size; row++) {
                try {
                    String songName = CommonUtil.deleteParenthesesEnd(CommonUtil.deleteSpaceAndUpperFirst(getString(sheet.getRow(row).getCell(0))));
                    String singer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(1)));
                    String language = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(2)));
                    String producer = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(3)));
                    String litigant = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(4)));

                    List<SongSimple> songs = mysqlSongSimpleRepository.findAllBySongName(songName);

                    if (songs != null) {
                        songs.forEach(item -> {
                            List<CodeName> singers = handlerMusicianMySQL(singer);
                            List<CodeName> producers = handlerMusicianMySQL(producer);
                            List<CodeName> litigants = handlerMusicianMySQL(producer);
                            if (fill2Mysql(item, singers, null, null, litigants, producers, null, null, null, null)) {
                                count.getAndIncrement();
                                System.out.println("更新歌曲id：" + item.getSongId() + " 出品：" + producer + " 权利人：" + litigant);
                            }
                        });

                        if (count.get() % 100 == 0) {
                            System.out.println("第" + row + "行，" + " 歌名：" + songName + " 歌星：" + singer + " 语种：" + language
                                    + " 出品：" + producer + " 权利人：" + litigant);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount.getAndIncrement();
                    errorRow.append(row).append(",");
                }
            }
            String tip = "更新歌曲总数：" + count.get() + " 出错数：" + errorCount.get() + " 出错行：" + errorRow.toString() + " 耗时：" + (System.currentTimeMillis() - time) / 1000;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
