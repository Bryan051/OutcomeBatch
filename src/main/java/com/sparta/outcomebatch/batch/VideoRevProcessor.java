package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoRev;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class VideoRevProcessor implements ItemProcessor<Video, VideoRev> {

    private final VideoBatchProcessorService videoBatchProcessorService;

    @Override
    public VideoRev process(Video video) throws Exception {
        LocalDate date = LocalDate.now();
//        LocalDate date = LocalDate.of(2024, 7, 9);
        // 오늘 계산값
        int dailyView = videoBatchProcessorService.countVideoViewsExcludingUserAndDate(video, date);

        // 어제까지 총합
        int lastTotalView = video.getTotalVideoView();

        // 일 정산
        double revenue = videoBatchProcessorService.calculateRevenue(lastTotalView, dailyView);

        // VideoRev 객체에 값 설정
        VideoRev videoRev = new VideoRev();
        videoRev.setDate(date);
        videoRev.setVideoId(video.getVidId());
        videoRev.setVideoRevenue(BigDecimal.valueOf(revenue));

        return videoRev;
    }
}