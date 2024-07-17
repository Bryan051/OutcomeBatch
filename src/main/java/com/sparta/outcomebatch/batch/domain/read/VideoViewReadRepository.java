package com.sparta.outcomebatch.batch.domain.read;

import com.sparta.outcomebatch.batch.domain.User;
import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface VideoViewReadRepository extends JpaRepository<VideoView,Long> {
    List<VideoView> findByUserIdAndVidId(User userId, Video vidId);

    List<VideoView> findTop2ByUserIdAndVidIdOrderByIdDesc(User user, Video video);

    // batch.videostats..video_view
    /*  해당 VideoView 데이터 중 해당 video를 등록한 userId를 제외한 기록만, 날짜별로 선택해서 카운트
        돌아가는거 확인. video_stats 의 video_view 로 배치.
        이후 video 안의 total_video_view 누적조회수 갱신까지.
    */
    @Query("SELECT COUNT(v) FROM VideoView v WHERE v.userId <> :#{#video.userId} AND v.vidId = :video AND FUNCTION('DATE', v.createdAt) = :date")
    int countVideoViewsExcludingUserAndDate(@Param("video") Video video, @Param("date") LocalDate date);

    // batch.videostats..play_time ->재생시간
    @Query("SELECT SUM(v.duration) FROM VideoView v WHERE v.userId <> :#{#video.userId} AND v.vidId = :video AND FUNCTION('DATE', v.createdAt) = :date")
    Long sumVideoViewDurationsExcludingUserAndDate(@Param("video") Video video, @Param("date") LocalDate date);

    // batch.video_rev..
    /*
    영상별 단가 10만 미만 1 / 50만 미만 1.1 / 100만 미만 1.3/ 100만 이상 1.5
    전날 일 누적조회수 범위 측정, 8일이 전날이라가정. 8일까지누적 조회수 48만. 9일의 일 조회수가 4만일경우
    전날 8일의 누적조회수별단가 1.1 곱하기 2만(총50만까지), 이후 남은 9일의 일 조회수2만 곱하기 1.3.
    videostats 의 조회수 * 단가
     */







}
