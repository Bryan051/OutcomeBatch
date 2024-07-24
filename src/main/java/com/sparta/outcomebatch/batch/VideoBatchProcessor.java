package com.sparta.outcomebatch.batch;

import com.sparta.outcomebatch.batch.domain.Video;
import com.sparta.outcomebatch.batch.domain.VideoStats;
import com.sparta.outcomebatch.batch.domain.read.VideoReadRepository;
import com.sparta.outcomebatch.batch.domain.write.VideoWriteRepository;
import com.sparta.outcomebatch.batch.service.VideoBatchProcessorService;
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
//        LocalDate date = LocalDate.of(2024, 7, 17);

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