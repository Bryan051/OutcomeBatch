package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.domain.write.VideoWriteRepository;
import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoStats;
import com.sparta.outcomebatch.batch.domain.read.VideoReadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VideoBatchProcessor implements ItemProcessor<Video, VideoStats> {

    private final VideoBatchProcessorService videoBatchProcessorService;
    private final VideoReadRepository videoReadRepository;
    private final VideoWriteRepository videoWriteRepository;

    @Override
    public VideoStats process(Video video) throws Exception {
        LocalDate date = LocalDate.now();
//        LocalDate date = LocalDate.of(2024, 7, 9);
        // 오늘 계산값
        int dailyView = videoBatchProcessorService.countVideoViewsExcludingUserAndDate(video, date);

        // 어제까지 총합
        int lastTotalView = video.getTotalVideoView();
        video.setTotalVideoView(dailyView + lastTotalView);

        // video 엔티티 업데이트
        videoWriteRepository.save(video);

        // 플레이 시간 계산
        Long playTime = videoBatchProcessorService.sumVideoViewDurationsExcludingUserAndDate(video, date);

        // VideoStats 객체에 값 설정
        VideoStats videoStats = new VideoStats();
        videoStats.setDate(date);
        videoStats.setVideoId(video.getVidId());
        videoStats.setVideoView(dailyView);
        videoStats.setPlayTime(playTime);

        return videoStats;
    }
}