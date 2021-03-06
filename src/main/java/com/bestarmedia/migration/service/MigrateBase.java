package com.bestarmedia.migration.service;

import com.bestarmedia.migration.misc.CommonUtil;
import com.bestarmedia.migration.model.mongo.CodeName;
import com.bestarmedia.migration.model.mongo.VideoFile;
import com.bestarmedia.migration.model.mysql.Song;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MigrateBase {


    public static CodeName getEdition(Integer videoType, int type) {
        switch (videoType) {
            case 1:
                return new CodeName(type == 2 ? 7 : 1, "MV");
            case 2:
                return new CodeName(type == 2 ? 8 : 3, "现场");
            case 3:
                return new CodeName(type == 2 ? 9 : 4, "综艺");
            case 4:
                return new CodeName(type == 2 ? 10 : 5, type == 2 ? "其他" : "尊享");
            case 7:
            case 8:
            case 9:
            case 10:
                return new CodeName(type == 2 ? 10 : 2, type == 2 ? "其他" : "重制");
            default:
                return new CodeName(type == 2 ? 10 : 6, "其他");
        }
    }


    public static List<CodeName> getIdNames(String ids, String names) {
        List<CodeName> idNames = new ArrayList<>();
        List<Integer> singerIds = new ArrayList<>();
        List<String> singerNames = new ArrayList<>();
        try {
            singerIds = CommonUtil.OBJECT_MAPPER.readValue(ids, new TypeReference<List<Integer>>() {
            });
        } catch (Exception e) {
        }
        try {
            singerNames = CommonUtil.OBJECT_MAPPER.readValue(names, new TypeReference<List<String>>() {
            });

        } catch (Exception e) {
        }
        try {
            int size = singerIds.size() >= singerNames.size() ? singerIds.size() : singerNames.size();
            for (int i = 0; i < size; i++) {
                idNames.add(new CodeName(i < singerIds.size() ? singerIds.get(i) : 0, i < singerNames.size() ? CommonUtil.deleteStartEndSpaceChar(singerNames.get(i)) : ""));
            }
        } catch (Exception e) {

        }
        return idNames;
    }


    VideoFile createFileDto(Song song, String original, String accompaniment, String lyric, String remark) {
        VideoFile file = new VideoFile();
        file.setCode(song.getSongId());
        if (!StringUtils.isEmpty(song.getMediaFilePath()) && song.getMediaFilePath().toLowerCase().endsWith(".mp4")) {//视频版本，旧版的音频歌曲放到音画版
            file.setFileName(song.getMediaFilePath().substring(song.getMediaFilePath().lastIndexOf("/") + 1));
            file.setFilePath("https://song-enterprise.oss-cn-shenzhen.aliyuncs.com/song/h264/" + file.getFileName());
            file.setLyricFilePath("");
            file.setResolutionWidth(song.getResolutionWidth());
            file.setResolutionHeight(song.getResolutionHeight());
            if (!StringUtils.isEmpty(song.getScoreStandardFilePath()) && !StringUtils.isEmpty(song.getCoordinateFilePath())) {
                file.setScoreFilePath(song.getScoreStandardFilePath());
                file.setCoordinatesFilePath(song.getCoordinateFilePath());
            } else {
                file.setScoreFilePath(null);
                file.setCoordinatesFilePath(null);
            }
            file.setVolume(song.getVolume());
        } else {
            file.setOriginalAudioFilePath(StringUtils.isEmpty(original) ?
                    ("https://song-enterprise.oss-cn-shenzhen.aliyuncs.com/song/h264/" + song.getMediaFilePath().substring(song.getMediaFilePath().lastIndexOf("/") + 1)) : original);
            file.setAccompanimentAudioFilePath(accompaniment);
            file.setLyricFilePath(lyric);
            file.setResolutionWidth(1920);
            file.setResolutionHeight(1080);
            file.setScoreFilePath(null);
            file.setCoordinatesFilePath(null);
            file.setVolume(80);
        }
        file.setFormatName("H264");
        file.setVideoType(song.getVideoTypeDetail() == null ? "" : song.getVideoTypeDetail().getName());
        file.setAudioTrack(song.getAudioTrack());
        file.setHot(0L);
        file.setRecommend(song.getSofthard());
        file.setStatus(1);
        file.setRemark(remark);
        return file;
    }
}
