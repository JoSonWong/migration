package com.bestarmedia.migration.repository.mysql;

import com.bestarmedia.migration.model.mysql.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MysqlSongRepository extends JpaRepository<Song, Integer>, JpaSpecificationExecutor<Song> {

    @Override
    <S extends Song> List<S> saveAll(Iterable<S> iterable);

    /**
     * 动态查询.
     */
    Page<Song> findAll(Specification<Song> var1, Pageable pageable);

    @Override
    List<Song> findAll(Specification<Song> specification, Sort sort);

    Song findSongBySongIdAndStatus(int sId, int status);

    Song findSongBySongId(int sId);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set local_exist_media_file=:fileStatus  where s_id=:id", nativeQuery = true)
    int updateSongFileStatus(@Param(value = "fileStatus") int fileStatus, @Param(value = "id") int id);

    int countAllByStatus(int status);

    int countAllByStatusAndLocalExistMediaFile(int status, int localExistMediaFile);

    @Query(value = "select * from vod_song as s where s.status=1 and s.local_exist_media_file=:hasSongFile order by s_id limit :startIndex, 500", nativeQuery = true)
    List<Song> getSongList(@Param(value = "hasSongFile") int hasSongFile, @Param(value = "startIndex") int startIndex);

    @Query(value = "select * from vod_song as s where s.status=1 order by s_id limit :startIndex, 500", nativeQuery = true)
    List<Song> getAllSongList(@Param(value = "startIndex") int startIndex);


    /**
     * 更新热度
     *
     * @param hot 热度
     * @param id  id
     * @return 影响条数
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set hot=:hot where s_id=:id", nativeQuery = true)
    int updateSongHot(@Param(value = "hot") int hot, @Param(value = "id") int id);


    /**
     * 更新状态
     *
     * @param status 状态
     * @param id     歌曲Id
     * @return 影响条数
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set status=:status where s_id=:id", nativeQuery = true)
    int updateStatus(@Param(value = "status") int status, @Param(value = "id") int id);

    @Query(value = "select * from vod_song where s_id in (:ids)", nativeQuery = true)
    List<Song> findSongListBySongIds(@Param(value = "ids") List<Integer> ids);


    /**
     * 更新推荐度
     *
     * @param softhard 推荐度
     * @param id       id
     * @return 影响条数
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set softhard=:softhard where s_id=:id", nativeQuery = true)
    int updateRecommend(@Param(value = "softhard") int softhard, @Param(value = "id") int id);

    /**
     * @param softhard
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set softhard=:softhard ", nativeQuery = true)
    int setRecommend(@Param(value = "softhard") int softhard);


    @Query(value = "select * from vod_song as s where s.status=1  and s.deleted_at is NULL ORDER BY s.song_name ASC, s.singer ASC,s.video_type ASC  limit :startIndex, :pageSize", nativeQuery = true)
    List<Song> getSongSameName(@Param(value = "startIndex") int startIndex, @Param(value = "pageSize") int pageSize);


    @Query(value = "select count(*) from vod_song as s where s.status=1 and s.deleted_at is NULL ORDER BY s.song_name ASC, s.singer ASC,s.video_type ASC ", nativeQuery = true)
    Integer getSongSameNameCount();


    List<Song> findAllBySongName(String songName);


    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update vod_song set local_lyric_file_path=:local_lyric_file_path ,lyric_file_md5=:lyric_file_md5  where s_id=:id", nativeQuery = true)
    int updateReleaseTimeAndTag(@Param(value = "local_lyric_file_path") String local_lyric_file_path, @Param(value = "lyric_file_md5") String lyric_file_md5, @Param(value = "id") int id);

    @Query(value = "select * from vod_song as s where s.ktv_net_code <> ''   and  s.status=1 and s.deleted_at is NULL ORDER BY s.song_name ASC, s.singer ASC,s.video_type ASC ", nativeQuery = true)
    List<Song> getUgc();


    @Query(value = "select * from vod_song as s where s.status=1 and  s.media_file_path not like '%.mp4' and s.deleted_at is NULL ORDER BY s.song_name ASC, s.singer ASC,s.video_type ASC ", nativeQuery = true)
    List<Song> getSongSameNameMp3();
}
