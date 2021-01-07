package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mysql.Musician;
import com.bestarmedia.migration.model.mysql.Song;
import com.bestarmedia.migration.repository.mongo.song.SongSongVersionRepository;
import com.bestarmedia.migration.repository.mysql.MysqlMusicianRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongRepository;
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

@Service
public class FillDataService {

    private final SongSongVersionRepository songSongVersionRepository;
//    private final VodSingerRepository vodSingerRepository;
//    private final VodSongVersionRepository vodSongVersionRepository;

    private final MysqlSongRepository mysqlSongRepository;
    private final MysqlMusicianRepository mysqlMusicianRepository;

    @Autowired
    public FillDataService(SongSongVersionRepository songSongVersionRepository,
                           MysqlSongRepository mysqlSongRepository, MysqlMusicianRepository mysqlMusicianRepository) {
        this.songSongVersionRepository = songSongVersionRepository;
        this.mysqlSongRepository = mysqlSongRepository;
        this.mysqlMusicianRepository = mysqlMusicianRepository;
    }

    public String fill(Integer indexFrom, Integer indexTo) {
        int updateNoIdSongCount = 0;
        int updateNoIdVersionCount = 0;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/song_message.xlsx");
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            sheet = wb.getSheetAt(0);
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
                System.out.println("第" + row + "行，id：" + id + " 歌名：" + songName + " 歌星：" + singer + " 词：" + lyricist + " 曲：" + composer
                        + " 诉讼：" + litigant + " 制作：" + producer + " 出品：" + publisher + " 发行日期：" + releaseTime);
                List<CodeName> singers = handlerMusicianMySQL(1, singer);
                List<CodeName> lyricists = handlerMusicianMySQL(2, lyricist);
                List<CodeName> composers = handlerMusicianMySQL(3, composer);
                List<CodeName> litigants = handlerMusicianMySQL(4, litigant);
                List<CodeName> producers = handlerMusicianMySQL(5, producer);
                List<CodeName> publishers = handlerMusicianMySQL(6, publisher);
                fill2Mysql(id, songName, singers, lyricists, composers, litigants, producers, publishers);
            }
            String tip = "更新歌曲总数：" + updateNoIdSongCount + " 更新版本总数：" + updateNoIdVersionCount;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void fill2Mysql(String id, String songName, List<CodeName> singers, List<CodeName> lyricists, List<CodeName> composers,
                            List<CodeName> litigants, List<CodeName> producers, List<CodeName> publishers) {
        if (!StringUtils.isEmpty(id)) {
            try {
                Song song = mysqlSongRepository.findSongBySongId(Integer.valueOf(id));
                if (song != null) {
                    song.setSongName(songName);
                    boolean update = false;
                    if (singers != null && !singers.isEmpty()) {
                        List<Integer> ids = new ArrayList<>();
                        List<String> names = new ArrayList<>();
                        singers.forEach(item -> {
                            ids.add(item.getCode());
                            names.add(item.getName());
                        });
                        song.setSingerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                        song.setSinger(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));
                        update = true;
                    }
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
                    if (update) {
                        Song save = mysqlSongRepository.save(song);
                        System.out.println("保存歌曲到MySQL：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            List<Song> songs = mysqlSongRepository.findAllBySongName(songName);
            if (songs != null) {
                songs.forEach(item -> {
                    if (isContainSinger(singers, item.getSinger())) {
                        fill2Mysql(item.getSongId() + "", songName, singers, lyricists, composers, litigants, producers, publishers);
                    }
                });
            }
        }
    }

    private boolean isContainSinger(List<CodeName> codeNames, String dbSingerName) {
        if (codeNames.isEmpty() && StringUtils.isEmpty(dbSingerName)) {
            return true;
        }
        try {
            List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(dbSingerName, new TypeReference<List<String>>() {
            });
            int i = 0;
            for (CodeName codeName : codeNames) {
                if (singerNames.size() > i) {
                    if (codeName.getName().trim().toUpperCase().equals(singerNames.get(i).trim().toUpperCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String fillReleaseTime() {
        XSSFWorkbook wb;
        XSSFSheet sheet;
        long count = 0;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/song_message.xlsx");
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            sheet = wb.getSheetAt(0);
            int size = sheet.getPhysicalNumberOfRows();
            int from = 1;

            //循环取每行的数据
            for (int row = from; row < size; row++) {
                String id = getString(sheet.getRow(row).getCell(0));
                String releaseTime = CommonUtil.deleteSpecialChar(getString(sheet.getRow(row).getCell(8)));
                System.out.println("第" + row + "行，id：" + id + " 发行日期：" + releaseTime);
                Date releaseDate = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    releaseDate = sdf.parse(releaseTime);
                } catch (Exception e) {
                }
                if (releaseDate != null) {
                    count = count + songSongVersionRepository.updateIssueTime(Integer.valueOf(id), releaseDate);
                }
            }
            String tip = "更新发行时间总数量：" + count;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private List<CodeName> handlerMusicianMySQL(int musicianType, String musicians) {
        List<CodeName> songSingers = new ArrayList<>();
        if (!StringUtils.isEmpty(musicians)) {
            String[] singers = musicians.split("\\|");
            for (String name : singers) {
                String n = CommonUtil.deleteSpaceAndUpperFirst(name);
                System.out.println("查询歌星：" + n + " 信息 >>>>>>>");
                Musician musician = mysqlMusicianRepository.findFirstByMusicianName(n);
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
        int updateNoIdSongCount = 0;
        int updateNoIdVersionCount = 0;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/MTV_yjx.xlsx");
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            sheet = wb.getSheetAt(0);
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

                List<Song> songs = mysqlSongRepository.findAllBySongName(songName);

                if (songs != null) {
                    songs.forEach(item -> {
                        if (isContainSinger(singer, item.getSinger())) {
                            List<CodeName> litigants = handlerMusicianMySQL(4, litigant);
                            List<CodeName> producers = handlerMusicianMySQL(5, producer);
                            fill2Mysql(item, litigants, producers);
                        }
                    });
                }

//                List<CodeName> singers = handlerMusicianMySQL(1, singer);
//                List<CodeName> lyricists = handlerMusicianMySQL(2, lyricist);
//                List<CodeName> composers = handlerMusicianMySQL(3, composer);
//                List<CodeName> litigants = handlerMusicianMySQL(4, litigant);
//                List<CodeName> producers = handlerMusicianMySQL(5, producer);
//                List<CodeName> publishers = handlerMusicianMySQL(6, publisher);
//                fill2Mysql(id, songName, singers, lyricists, composers, litigants, producers, publishers);
            }
            String tip = "更新歌曲总数：" + updateNoIdSongCount + " 更新版本总数：" + updateNoIdVersionCount;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private boolean isContainSinger(String excelSingerName, String dbSingerName) {
        if (StringUtils.isEmpty(excelSingerName) && StringUtils.isEmpty(dbSingerName)) {
            return true;
        }
        try {
            String[] excelSingers = excelSingerName.split("\\|");
            List<String> singerNames = CommonUtil.OBJECT_MAPPER.readValue(dbSingerName, new TypeReference<List<String>>() {
            });
            int i = 0;
            for (String singer : excelSingers) {
                if (singerNames.size() > i) {
                    if (singer.trim().toUpperCase().equals(singerNames.get(i).trim().toUpperCase())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private void fill2Mysql(Song song, List<CodeName> litigants, List<CodeName> producers) {
        try {
            if (song != null) {
                boolean update = false;
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
                if (update) {
                    Song save = mysqlSongRepository.save(song);
                    System.out.println("保存歌曲到MySQL：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String fillLY(Integer indexFrom, int indexTo) {
        int updateNoIdSongCount = 0;
        int updateNoIdVersionCount = 0;
        XSSFWorkbook wb;
        XSSFSheet sheet;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "templates/ly.xlsx");
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            sheet = wb.getSheetAt(0);
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

                List<Song> songs = mysqlSongRepository.findAllBySongName(songName);

                if (songs != null) {
                    songs.forEach(item -> {
                        if (isContainSinger(singer, item.getSinger())) {
                            List<CodeName> lyricists = handlerMusicianMySQL(2, lyricist);
                            List<CodeName> composers = handlerMusicianMySQL(3, composer);
                            List<CodeName> litigants = handlerMusicianMySQL(4, litigant);
                            List<CodeName> producers = handlerMusicianMySQL(5, producer);
                            fill2Mysql(item, lyricists, composers, litigants, producers);
                        } else {
                            System.out.println("同歌名[" + item.getSongName() + "]不同歌星 EXCEL [" + singer + "] db [" + item.getSinger() + "]");
                        }
                    });
                }
            }
            String tip = "更新歌曲总数：" + updateNoIdSongCount + " 更新版本总数：" + updateNoIdVersionCount;
            System.out.println(tip);
            return tip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void fill2Mysql(Song song, List<CodeName> lyricists, List<CodeName> composers, List<CodeName> litigants, List<CodeName> producers) {
        try {
            if (song != null) {
                boolean update = false;
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
                if (update) {
                    Song save = mysqlSongRepository.save(song);
                    System.out.println("保存歌曲到MySQL：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
