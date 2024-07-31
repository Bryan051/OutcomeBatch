package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoStats;
import com.sparta.outcomebatch.batch.domain.read.VideoReadRepository;
import com.sparta.outcomebatch.batch.domain.write.VideoWriteRepository;
import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoUpdateProcessor implements ItemProcessor<Video, Video> {

    private final VideoBatchProcessorService videoBatchProcessorService;

    @Override
    @Transactional
    public Video process(Video video) throws Exception {
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(2);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // 비디오 엔티티 업데이트 로직
        int dailyView = videoBatchProcessorService.countVideoViewsExcludingUserAndDate(video, startDate, endDate);
        // 기존 총합
        int lastTotalView = video.getTotalVideoView();
        video.setTotalVideoView(dailyView + lastTotalView);
        return video;
    }
}