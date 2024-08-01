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
public class VideoBatchProcessor implements ItemProcessor<Video, VideoStats> {

    private final VideoBatchProcessorService videoBatchProcessorService;
    private final VideoReadRepository videoReadRepository;
    private final VideoWriteRepository videoWriteRepository;

    @Override
    @Transactional(transactionManager = "streamingTransactionManager", readOnly = true)
    public VideoStats process(Video video) throws Exception {
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now().minusDays(2);

        // 오늘 계산값
        int dailyView = videoBatchProcessorService.countVideoViewsExcludingUserAndDate(video, startDate, endDate);
//        // 확인을 위한 로그 추가
//        log.info("Processing video: {} from {} to {}", video.getVidId(), startDate, endDate);

        // 플레이 시간 계산
        Long playTime = videoBatchProcessorService.sumVideoViewDurationsExcludingUserAndDate(video, startDate, endDate);
//        // 확인을 위한 로그 추가
//        log.info("Calculated daily view count for video {}: {}", video.getVidId(), dailyView);
//        log.info("Calculated play time for video {}: {}", video.getVidId(), playTime);

        // VideoStats 객체에 값 설정
        VideoStats videoStats = new VideoStats();
        videoStats.setDate(endDate);
        videoStats.setVideoId(video.getVidId());
        videoStats.setVideoView(dailyView);
        videoStats.setPlayTime(playTime);

        return videoStats;
    }
}




//        // 오늘 날짜에 해당하는 VideoView만 필터링
//        List<VideoView> videoViews = video.getVideoViews().stream()
//                .filter(vv -> vv.getCreatedAt().equals(date))
//                .collect(Collectors.toList());
//
//        // Video의 UserId와 겹치는 VideoView 제외
//        int dailyView = (int) videoViews.stream()
//                .filter(vv -> !vv.getUserId().equals(video.getUserId()))
//                .count();
//
//        // 플레이 시간 계산
//        Long playTime = videoViews.stream()
//                .filter(vv -> !vv.getUserId().equals(video.getUserId()))
//                .mapToLong(VideoView::getDuration)
//                .sum();
//
//        // video 엔티티 업데이트
//        video.setTotalVideoView(video.getTotalVideoView() + dailyView);
//        videoWriteRepository.save(video);