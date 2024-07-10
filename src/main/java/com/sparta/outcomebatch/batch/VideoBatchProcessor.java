package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
import com.sparta.outcomebatch.entity.Video;
import com.sparta.outcomebatch.entity.VideoRev;
import com.sparta.outcomebatch.entity.VideoStats;
import com.sparta.outcomebatch.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.stat.Statistics;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VideoBatchProcessor implements ItemProcessor<Video, VideoStats> {

    private final VideoBatchProcessorService videoBatchProcessorService;
    private final VideoRepository videoRepository;

    @Override
    public VideoStats process(Video video) throws Exception {
//        LocalDate date = LocalDate.now();
        LocalDate date = LocalDate.of(2024, 7, 9);
        // 오늘 계산값
        int dailyView = videoBatchProcessorService.countVideoViewsExcludingUser(video, date);

        // 어제까지 총합
        int lastTotalView = video.getTotalVideoView();
        video.setTotalVideoView(dailyView + lastTotalView);

        // 플레이 시간 계산
        Long playTime = videoBatchProcessorService.sumVideoViewDurationsExcludingUserAndDate(video, date);

        // VideoStats 객체에 값 설정
        VideoStats videoStats = new VideoStats();
        videoStats.setDate(date);
        videoStats.setVideoId(video.getVidId());
        videoStats.setVideoView(dailyView);
        videoStats.setPlayTime(playTime);

        // video 엔티티 업데이트
        videoRepository.save(video);

        return videoStats;
    }
}