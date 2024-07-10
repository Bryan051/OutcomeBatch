package com.sparta.outcomebatch.service;

import com.sparta.outcomebatch.dto.BatchVideoRequestDto;
import com.sparta.outcomebatch.entity.Video;
import com.sparta.outcomebatch.repository.VideoRepository;
import com.sparta.outcomebatch.repository.VideoViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BatchVideoService {

    private final VideoViewRepository videoViewRepository;
    private final VideoRepository videoRepository;

    // video Stats
    @Transactional
    public int countVideoViewsExcludingUser(Video video, LocalDate date) {
        return videoViewRepository.countVideoViewsExcludingUserAndDate(video, date);
    }

    @Transactional
    public Long sumVideoViewDurationsExcludingUserAndDate(Video video, LocalDate date) {
        return videoViewRepository.sumVideoViewDurationsExcludingUserAndDate(video, date);
    }

    @Transactional
    public String countAndCalculateVideoViews(BatchVideoRequestDto batchVideoRequestDto) {
        Video video = videoRepository.findById(batchVideoRequestDto.getVidId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        // 오늘 계산값
        int dailyView = countVideoViewsExcludingUser(video, batchVideoRequestDto.getDate());

        // 어제까지 총합
        int lastTotalView = video.getTotalVideoView();
        video.setTotalVideoView(dailyView + lastTotalView);

        // 일 정산
        double revenue = 0;
        if (lastTotalView < 100000) {
            int firstHalfView = 99999 - lastTotalView; // 10만까지 남은 뷰
            if (dailyView <= firstHalfView) {
                revenue = dailyView;
            } else {
                revenue += firstHalfView; // * 1원
                int secondHalfView = dailyView - firstHalfView; //10만 넘김
                revenue += secondHalfView * 1.1;
            }
        } else if (lastTotalView < 500000) {
            int firstHalfView = 499999 - lastTotalView; // 50만 미만
            if (dailyView <= firstHalfView) {
                revenue = dailyView * 1.1;
            } else {
                revenue += firstHalfView * 1.1;
                int secondHalfView = dailyView - firstHalfView; //50만 넘김
                revenue += secondHalfView * 1.3;
            }
        } else if (lastTotalView < 1000000) {
            int firstHalfView = 999999 - lastTotalView; // 100만 미만
            if (dailyView <= firstHalfView) {
                revenue = dailyView * 1.3;
            } else {
                revenue += firstHalfView * 1.3;
                int secondHalfView = dailyView - firstHalfView; //100만 넘김
                revenue += secondHalfView * 1.5;
            }
        } else {
            revenue = dailyView * 1.5;
        }

        videoRepository.save(video);

        return "일 조회수: " + dailyView + " / 일 정산액: " + revenue;
    }

}
