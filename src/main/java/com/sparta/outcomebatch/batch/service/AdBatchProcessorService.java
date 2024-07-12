package com.sparta.outcomebatch.batch.service;

import com.sparta.outcomebatch.streaming.domain.VideoAd;
import com.sparta.outcomebatch.streaming.domain.AdViewRepository;
import com.sparta.outcomebatch.streaming.domain.VideoAdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdBatchProcessorService {

    public final VideoAdRepository videoAdRepository;
    private final AdViewRepository adViewRepository;

    @Transactional
    public int countAdViewsByVideoAdAndDate(VideoAd videoAd, LocalDate date) {
        List<VideoAd> videoAds = Collections.singletonList(videoAd);
        return (int) adViewRepository.countByVideoAdsAndDate(videoAds, date);
    }

    public double calculateRevenue(int lastTotalView, int adViewCount) {
        double revenue = 0;
        if (lastTotalView < 100000) {
            int firstHalfView = 99999 - lastTotalView; // 10만까지 남은 뷰
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 10;
            } else {
                revenue += firstHalfView * 10; // * 1원
                int secondHalfView = adViewCount - firstHalfView; //10만 넘김
                revenue += secondHalfView * 12;
            }
        } else if (lastTotalView < 500000) {
            int firstHalfView = 499999 - lastTotalView; // 50만 미만
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 12;
            } else {
                revenue += firstHalfView * 12;
                int secondHalfView = adViewCount - firstHalfView; //50만 넘김
                revenue += secondHalfView * 15;
            }
        } else if (lastTotalView < 1000000) {
            int firstHalfView = 999999 - lastTotalView; // 100만 미만
            if (adViewCount <= firstHalfView) {
                revenue = adViewCount * 15;
            } else {
                revenue += firstHalfView * 15;
                int secondHalfView = adViewCount - firstHalfView; //100만 넘김
                revenue += secondHalfView * 20;
            }
        } else {
            revenue = adViewCount * 20;
        }
        return revenue;
    }
}