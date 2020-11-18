package com.bestarmedia.migration.service;


import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mysql.Musician;
import com.bestarmedia.migration.model.mysql.Song;
import com.bestarmedia.migration.repository.mongo.song.SongSongVersionRepository;
import com.bestarmedia.migration.repository.mysql.MysqlMusicianRepository;
import com.bestarmedia.migration.repository.mysql.MysqlSongRepository;
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

    public String fill(Integer index) {
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
            if (index > 0) {
                from = index;
                size = from + 1;
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
                    List<Integer> ids = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    singers.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setSingerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setSinger(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    ids.clear();
                    names.clear();
                    lyricists.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setLyricistMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setLyricist(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    ids.clear();
                    names.clear();
                    composers.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setComposerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setComposer(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    ids.clear();
                    names.clear();
                    litigants.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setLitigantMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setLitigant(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    ids.clear();
                    names.clear();
                    producers.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setProducerMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setProducer(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    ids.clear();
                    names.clear();
                    publishers.forEach(item -> {
                        ids.add(item.getCode());
                        names.add(item.getName());
                    });
                    song.setPublisherMid(CommonUtil.OBJECT_MAPPER.writeValueAsString(ids));
                    song.setPublisher(CommonUtil.OBJECT_MAPPER.writeValueAsString(names));

                    Song save = mysqlSongRepository.save(song);

                    System.out.println("保存歌曲到MySQL：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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


//    private void fill2MongoDb(String id, String songName, List<CodeName> singers, List<CodeName> lyricists, List<CodeName> composers,
//                              List<CodeName> litigants, List<CodeName> producers, List<CodeName> publishers, Date releaseDate) {
//        if (StringUtils.isEmpty(id)) {//没歌曲id
////                    boolean isUpdatedSong = false;
////                    noIdCount++;
////                    if (!singers.isEmpty()) {
////                        List<String> names = new ArrayList<>();
////                        singers.forEach(item -> names.add(item.getName()));
////                        List<VodSong> songs = vodSongRepository.findByNameAndSinger(songName, names);
////                        if (songs != null && songs.size() == 1) {
////                            VodSong song = songs.get(0);
////                            song.setSongName(songName);
////                            song.setSinger(singers);
////                            song.setLyricist(lyricists);
////                            song.setComposer(composers);
////                            VodSong saveSong = vodSongRepository.update(song);
////                            System.out.println("无歌曲ID，通过歌名+歌星保存歌曲：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveSong));
////                            isUpdatedSong = true;
////                            updateNoIdSongCount++;
////
////                            List<SongVersion> versions = vodSongVersionRepository.findSongVersion(song.getCode());
////                            if (versions != null && versions.size() == 1) {
////                                SongVersion songVersion = versions.get(0);
////                                songVersion.setLitigant(litigants);
////                                songVersion.setProducer(producers);
////                                songVersion.setPublisher(publishers);
////                                songVersion.setIssueTime(releaseDate);
////                                SongVersion saveVersion = vodSongVersionRepository.update(songVersion.getCode(), litigants, producers, publishers, releaseDate);
////                                System.out.println("无歌曲ID，通过歌名+歌星保存版本：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveVersion));
////
////                                updateNoIdVersionCount++;
////                            }
////                        }
////
////
////                        if (!isUpdatedSong) {
////                            songs = vodSongRepository.findByName(songName);
////                            if (songs != null && songs.size() == 1) {
////                                VodSong song = songs.get(0);
////                                song.setSongName(songName);
////                                song.setSinger(singers);
////                                song.setLyricist(lyricists);
////                                song.setComposer(composers);
////                                VodSong saveSong = vodSongRepository.update(song);
////                                System.out.println("无歌曲ID，通过歌名保存歌曲：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveSong));
////                                updateNoIdSongCount++;
////
////                                List<SongVersion> versions = vodSongVersionRepository.findSongVersion(song.getCode());
////                                if (versions != null && versions.size() == 1) {
////                                    SongVersion songVersion = versions.get(0);
////                                    songVersion.setLitigant(litigants);
////                                    songVersion.setProducer(producers);
////                                    songVersion.setPublisher(publishers);
////                                    songVersion.setIssueTime(releaseDate);
////                                    SongVersion saveVersion = vodSongVersionRepository.update(songVersion.getCode(), litigants, producers, publishers, releaseDate);
////                                    System.out.println("无歌曲ID，通过歌名保存版本：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveVersion));
////
////                                    updateNoIdVersionCount++;
////                                }
////                            }
////                        }
////                    }
//        } else {//有歌曲ID
//            VodSongVersion songVersion = vodSongVersionRepository.findBySongCodeOld(Integer.valueOf(id));
//            if (songVersion != null) {
//                try {
//                    VodSong song = vodSongRepository.findByCodeNotStatus(songVersion.getSongCode());
//                    if (song.getSongName().equalsIgnoreCase(songName)) {//歌名匹配得上才更新
////                        songVersion.setLitigant(litigants);
////                        songVersion.setProducer(producers);
////                        songVersion.setPublisher(publishers);
////                        songVersion.setIssueTime(releaseDate);
//                        VodSongVersion saveVersion = vodSongVersionRepository.update(songVersion.getCode(), litigants, producers, publishers, releaseDate);
//                        System.out.println("保存版本到Mongo：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveVersion));
//                        song.setSongName(songName);
//                        song.setSinger(singers);
//                        song.setLyricist(lyricists);
//                        song.setComposer(composers);
//                        VodSong saveSong = vodSongRepository.update(song);
//                        System.out.println("保存歌曲到Mongo：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(saveSong));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

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


//    private List<CodeName> handlerMusicianMongo(int musicianType, String musicians) {
//        List<CodeName> songSingers = new ArrayList<>();
//        if (!StringUtils.isEmpty(musicians)) {
//            String[] singers = musicians.split("\\|");
//            for (String name : singers) {
//                String n = CommonUtil.deleteSpaceAndUpperFirst(name);
//                VodSinger singerModel = vodSingerRepository.findSingerByName(n);
//                if (singerModel == null) {//新建歌手信息
//                    CodeName commonSimple = new CodeName();
//                    commonSimple.setCode(0);
//                    commonSimple.setName(n);
//                    songSingers.add(commonSimple);
////                        int code = vodSingerRepository.findLastCode() + 1;
////                        Singer s = new Singer();
////                        s.setCode(code);
////                        s.setMusicianType(new ArrayList<>(1));
////                        s.setSimplicity();
////                        s.setMusicianName(n);
////                        s.setWordCount();
////                        s.setHot(0);
////                        s.setSex();
////                        s.setRole(0);
////                        s.setPart();
////                        s.setImgFilePath();
////                        s.setStatus(1);
////                        s.setAlias();
//                } else {
//                    boolean exitType = false;
//                    for (Integer type : singerModel.getMusicianType()) {
//                        if (type == musicianType) {
//                            exitType = true;
//                        }
//                    }
//                    if (!exitType) {//添加身份
//                        List<Integer> musicianTypes = singerModel.getMusicianType();
//                        musicianTypes.add(musicianType);
//                        musicianTypes.sort(Comparator.naturalOrder());
//                        singerModel.setMusicianType(musicianTypes);
//                        VodSinger save = vodSingerRepository.replace(singerModel);
//                        try {
//                            System.out.println("保存音乐人：" + CommonUtil.OBJECT_MAPPER.writeValueAsString(save));
//
//                        } catch (Exception e) {
//
//                        }
//                        CodeName commonSimple = new CodeName();
//                        commonSimple.setCode(save.getCode());
//                        commonSimple.setName(save.getMusicianName());
//
//                        songSingers.add(commonSimple);
//                    } else {
//                        CodeName commonSimple = new CodeName();
//                        commonSimple.setCode(singerModel.getCode());
//                        commonSimple.setName(singerModel.getMusicianName());
//                        songSingers.add(commonSimple);
//                    }
//                }
//            }
//        }
//        return songSingers;
//    }

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
}
